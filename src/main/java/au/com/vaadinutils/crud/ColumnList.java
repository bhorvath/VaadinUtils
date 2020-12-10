package au.com.vaadinutils.crud;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.metamodel.SingularAttribute;

/**
 * @deprecated Replaced in V14 migration.
 */
public class ColumnList {
    List<SingularAttribute<? extends Object, ? extends Object>> columns = new ArrayList<SingularAttribute<? extends Object, ? extends Object>>();

    public ColumnList(@SuppressWarnings("unchecked") SingularAttribute<? extends Object, ? extends Object>... columns) {
        for (SingularAttribute<? extends Object, ? extends Object> column : columns) {
            this.columns.add(column);
        }
    }

    public List<SingularAttribute<? extends Object, ? extends Object>> getList() {
        return columns;
    }

}
