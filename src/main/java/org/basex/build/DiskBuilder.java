package org.basex.build;

import org.basex.core.MainOptions;
import org.basex.core.StaticOptions;
import org.basex.core.cmd.DropDB;
import org.basex.data.Data;
import org.basex.data.DiskData;
import org.basex.data.MetaData;
import org.basex.index.name.Names;
import org.basex.io.IO;
import org.basex.io.IOFile;
import org.basex.io.in.DataInput;
import org.basex.io.out.DataOutput;
import org.basex.io.out.TableOutput;
import org.basex.io.random.TableAccess;
import org.basex.io.random.TableDiskAccess;
import org.basex.util.Compress;
import org.basex.util.Inline;
import org.basex.util.Prop;
import org.basex.util.Util;

import java.io.IOException;

import static org.basex.data.DataText.*;
import static org.basex.data.DataText.DATATMP;

public final class DiskBuilder extends Builder {
    /** Database table. */
    private DataOutput tout;
    /** Database texts. */
    private DataOutput xout;
    /** Database values. */
    private DataOutput vout;
    /** Output stream for temporary values. */
    private DataOutput sout;

    /** Static options. */
    private final StaticOptions sopts;
    /** Debug counter. */
    private int c;

    /**
     * Constructor.
     * @param name name of database
     * @param parser parser
     * @param sopts static options
     * @param opts main options
     */
    public DiskBuilder(final String name, final Parser parser, final StaticOptions sopts,
                       final MainOptions opts) {
        super(name, parser);
        this.sopts = sopts;
        meta = new MetaData(dbName, opts, sopts);
    }

    @Override
    public DiskData build() throws IOException {
        meta.assign(parser);

        // calculate optimized output buffer sizes to reduce disk fragmentation
        final Runtime rt = Runtime.getRuntime();
        final long max = Math.min(1 << 22, rt.maxMemory() - rt.freeMemory() >> 2);
        int bs = (int) Math.min(meta.inputsize, max);
        bs = Math.max(IO.BLOCKSIZE, bs - bs % IO.BLOCKSIZE);

        // drop old database (if available) and create new one
        DropDB.drop(dbName, sopts);
        sopts.dbPath(dbName).md();

        elemNames = new Names(meta);
        attrNames = new Names(meta);
        try {
            try {
                tout = new DataOutput(new TableOutput(meta, DATATBL));
                xout = new DataOutput(meta.dbFile(DATATXT), bs);
                vout = new DataOutput(meta.dbFile(DATAATV), bs);
                sout = new DataOutput(meta.dbFile(DATATMP), bs);
                parse();
            } finally {
                if(tout != null) tout.close();
                if(xout != null) xout.close();
                if(vout != null) vout.close();
                if(sout != null) sout.close();
            }

            // copy temporary values into database table
            final IOFile tmpFile = meta.dbFile(DATATMP);
            try(DataInput in = new DataInput(tmpFile)) {
                final TableAccess ta = new TableDiskAccess(meta, true);
                try {
                    for(; spos < ssize; ++spos) ta.write4(in.readNum(), 8, in.readNum());
                } finally {
                    ta.close();
                }
            }
            tmpFile.delete();

            // return database instance. build will be finalized when this instance is closed
            meta.dirty = true;
            return new DiskData(meta, elemNames, attrNames, path, nspaces);

        } catch(final Throwable th) {
            DropDB.drop(meta.name, sopts);
            throw th;
        }
    }

    @Override
    protected void addDoc(final byte[] value) throws IOException {
        tout.write1(Data.DOC);
        tout.write2(0);
        tout.write5(textRef(value, true));
        tout.write4(0);
        tout.write4(meta.size++);
    }

    @Override
    protected void addElem(final int dist, final int nameId, final int asize, final int uriId,
                           final boolean ne) throws IOException {

        tout.write1(asize << 3 | Data.ELEM);
        tout.write2((ne ? 1 << 15 : 0) | nameId);
        tout.write1(uriId);
        tout.write4(dist);
        tout.write4(asize);
        tout.write4(meta.size++);

        if(Prop.debug && (c++ & 0x7FFFF) == 0) Util.err(".");
    }

    @Override
    protected void addAttr(final int nameId, final byte[] value, final int dist, final int uriId)
            throws IOException {

        tout.write1(dist << 3 | Data.ATTR);
        tout.write2(nameId);
        tout.write5(textRef(value, false));
        tout.write4(uriId);
        tout.write4(meta.size++);
    }

    @Override
    protected void addText(final byte[] value, final int dist, final byte kind) throws IOException {
        tout.write1(kind);
        tout.write2(0);
        tout.write5(textRef(value, true));
        tout.write4(dist);
        tout.write4(meta.size++);
    }

    @Override
    protected void setSize(final int pre, final int size) throws IOException {
        sout.writeNum(pre);
        sout.writeNum(size);
        ++ssize;
    }

    /**
     * Calculates the text offset and writes the text value.
     * @param value value to be inlined
     * @param text text/attribute flag
     * @return inline value or text position
     * @throws IOException I/O exception
     */
    private long textRef(final byte[] value, final boolean text) throws IOException {
        // try to inline value
        final long inlined = Inline.pack(value);
        if(inlined != 0) return inlined;

        // store text to heap file
        final byte[] packed = Compress.pack(value);
        final DataOutput store = text ? xout : vout;
        final long offset = store.size();
        store.writeToken(packed);
        return packed != value ? Compress.COMPRESS | offset : offset;
    }
}
