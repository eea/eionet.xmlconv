package eionet.gdem.data.schemata;

import java.util.List;

/**
 *
 */
public interface SchemaDao {
    Schema insert(Schema schema);
    Schema findById(Integer id);
    Schema update(Schema schema);
    void delete(Schema schema);
    List<Schema> findAll();
}
