package org.basex.core.cmd;

import org.basex.build.DirParser;
import org.basex.build.DiskBuilder;
import org.basex.build.MemBuilder;
import org.basex.build.Parser;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.Databases;
import org.basex.core.MainOptions;
import org.basex.core.jobs.JobException;
import org.basex.core.locks.Locking;
import org.basex.core.locks.Locks;
import org.basex.core.parse.CmdBuilder;
import org.basex.core.parse.Commands;
import org.basex.core.users.Perm;
import org.basex.data.Data;
import org.basex.io.IO;
import org.basex.io.IOStream;
import org.basex.io.in.LookupInput;
import org.basex.util.Util;

import java.io.IOException;
import java.io.InputStream;

import static org.basex.core.Text.*;

public final class CreateDB extends ACreate {
    /** Parser instance. */
    private Parser parser;

    /**
     * Default constructor.
     * Input can be set via {@link #setInput(InputStream)}.
     * @param name name of database
     */
    public CreateDB(final String name) {
        this(name, null);
    }

    /**
     * Constructor, specifying an initial database input.
     * @param name name of database
     * @param input input reference (local/remote file path or XML string; can be {@code null})
     */
    public CreateDB(final String name, final String input) {
        super(name, input);
    }

    /**
     * Attaches a parser.
     * @param prsr input parser
     */
    public void setParser(final Parser prsr) {
        parser = prsr;
    }

    @Override
    protected boolean run() {
        final String name = args[0];
        if(!Databases.validName(name)) return error(NAME_INVALID_X, name);

        // choose parser and input
        IO source;
        try {
            source = sourceToIO(name);
            if(in != null) {
                final LookupInput li = new LookupInput(source.inputStream());
                source = li.lookup() == -1 ? null : new IOStream(li, source.name());
            }
        } catch(final IOException ex) {
            return error(Util.message(ex));
        }

        try {
            // create parser instance
            if(source != null) {
                if(!source.exists()) return error(RES_NOT_FOUND_X, source);
                parser = new DirParser(source, options);
            } else if(parser == null) {
                parser = Parser.emptyParser(options);
            }

            // close open database
            Close.close(context);

            final Data data;
            if(options.get(MainOptions.MAINMEM)) {
                // create main memory instance
                try {
                    data = pushJob(new MemBuilder(name, parser)).build();
                } finally {
                    popJob();
                }
                context.openDB(data);
                context.datas.pin(data);
            } else {
                if(context.pinned(name)) return error(DB_PINNED_X, name);

                // create disk-based instance
                final DiskBuilder builder = new DiskBuilder(name, parser, soptions, options);
                pushJob(builder);
                try {
                    builder.binaryDir(soptions.dbPath(name)).build().close();
                } finally {
                    popJob();
                }

                // second step: open database and create index structures
                final Open open = new Open(name);
                if(!open.run(context)) return error(open.info());

                data = context.data();
            }

            if(!update(data, new ACreate.Code() {
                @Override
                boolean run() throws IOException {
                    CreateIndex.create(data, CreateDB.this);
                    return info(parser.info() + DB_CREATED_X_X, name, jc().performance);
                }
            })) return false;

            if(options.get(MainOptions.CREATEONLY)) Close.close(context);
            return true;

        } catch(final JobException ex) {
            throw ex;
        } catch(final IOException ex) {
            return error(Util.message(ex));
        } catch(final Exception ex) {
            // known exceptions:
            // - IllegalArgumentException (UTF8, zip files)
            Util.stack(ex);
            return error(NOT_PARSED_X, parser.source);
        }
    }

    @Override
    public void addLocks() {
        final Locks locks = jc().locks;
        locks.reads.add(Locking.CONTEXT);
        locks.writes.add(args[0]);
    }

    /**
     * Returns a database instance from the specified parser.
     * @param name name of the database
     * @param parser input parser
     * @param ctx database context
     * @param options main options
     * @return new database instance
     * @throws IOException I/O exception
     */
    public static synchronized Data create(final String name, final Parser parser, final Context ctx,
                                           final MainOptions options) throws IOException {
        return create(name, parser, ctx, options, options.get(MainOptions.MAINMEM));
    }

    /**
     * Creates a new database instance, using the specified parser.
     * @param name name of the database
     * @param parser input parser
     * @param ctx database context
     * @param options main options
     * @param mem create main-memory instance
     * @return new database instance
     * @throws IOException I/O exception
     */
    public static synchronized Data create(final String name, final Parser parser, final Context ctx,
                                           final MainOptions options, final boolean mem) throws IOException {

        // check permissions
        if(!ctx.user().has(Perm.CREATE)) throw new BaseXException(PERM_REQUIRED_X, Perm.CREATE);

        // create main-memory or disk-based database instance
        final Data data;
        if(mem) {
            data = MemBuilder.build(name, parser);
        } else {
            // database is currently locked by another job
            if(ctx.pinned(name)) throw new BaseXException(DB_PINNED_X, name);
            new DiskBuilder(name, parser, ctx.soptions, options).build().close();
            data = Open.open(name, ctx, options);
        }

        CreateIndex.create(data, null);
        return data;
    }

    @Override
    public void build(final CmdBuilder cb) {
        cb.init(Commands.Cmd.CREATE + " " + Commands.CmdCreate.DB).arg(0).add(1);
    }
}
