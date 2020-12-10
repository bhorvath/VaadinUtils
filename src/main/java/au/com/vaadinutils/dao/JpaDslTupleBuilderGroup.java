package au.com.vaadinutils.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Tuple;
import javax.persistence.metamodel.SingularAttribute;

import au.com.vaadinutils.dao.JpaBaseDao.Condition;

/**
 * Sometimes it is faster to run multiple queries returning the same entity than
 * it is to run a single (potentially slow) query with multiple joins and
 * clauses. This class can be used to group the queries and return the results
 * from them in a single set.
 * 
 * Usage example:
 * 
 * <pre>
 * <code>
 * 	final JpaDslTupleBuilderGroup<TblSalesCustCallItem> queryGroup = new JpaDslTupleBuilderGroup<>(
 * 			TblSalesCustCallItem.class);
 * 	queryGroup.multiselect(TblSalesCustCallItem_.iid);
 * 	queryGroup.setCommon(new JpaDslTupleBuilderGroupCommon<TblSalesCustCallItem>()
 * 	{
 * 		&#64;Override
 * 		public void conditionsWillBeAdded(JpaDslTupleBuilder<TblSalesCustCallItem> builder,
 * 				List<Condition<TblSalesCustCallItem>> conditions)
 * 		{
 * 			conditions.add(builder.eq(TblSalesCustCallItem_.contact, contact));
 * 		}
 * 	});
 * 
 * 	queryGroup.addItem(new JpaDslTupleBuilderGroupItem<TblSalesCustCallItem>()
 * 	{
 * 		&#64;Override
 * 		public void conditionsWillBeAdded(JpaDslTupleBuilder<TblSalesCustCallItem> builder,
 * 				List<Condition<TblSalesCustCallItem>> conditions)
 * 		{
 * 			conditions.add(builder.eq(TblSalesCustCallItem_.salesperson, salesperson));
 * 		}
 * 	});
 * 
 * final List<Long> itemIds = new ArrayList<>();
 * for (Tuple result : queryGroup.getResults())
 * {
 * 			itemIds.add(queryGroup.get(result, TblSalesCustCallItem_.iid));
 * }
 * </code>
 * </pre>
 *
 */
public class JpaDslTupleBuilderGroup<E> {
    private List<JpaDslTupleBuilderGroupItem<E>> builders = new ArrayList<>();
    private JpaDslTupleBuilderGroupCommon<E> common;
    private Class<E> entityClass;
    private List<Tuple> results;
    private boolean distinct = false;
    private Map<SingularAttribute<E, ?>, Integer> multiselects = new LinkedHashMap<>();
    private int positionCounter = 0;
    private List<JpaDslOrder> orders = new ArrayList<>();

    public JpaDslTupleBuilderGroup(final Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public void addItem(JpaDslTupleBuilderGroupItem<E> builder) {
        builders.add(builder);
    }

    public interface JpaDslTupleBuilderGroupItem<E> {
        public void conditionsWillBeAdded(final JpaDslTupleBuilder<E> builder, final List<Condition<E>> conditions);
    }

    public void setCommon(JpaDslTupleBuilderGroupCommon<E> common) {
        this.common = common;
    }

    public interface JpaDslTupleBuilderGroupCommon<E> {
        public void conditionsWillBeAdded(final JpaDslTupleBuilder<E> builder, final List<Condition<E>> conditions);
    }

    public <T> void multiselect(SingularAttribute<E, T> attribute) {
        multiselects.put(attribute, positionCounter++);
    }

    public <T> T get(final Tuple tuple, final SingularAttribute<E, T> attribute) {
        final Integer tuplePosition = multiselects.get(attribute);
        return tuple.get(tuplePosition, attribute.getBindableJavaType());
    }

    public Object get(final Tuple tuple, final String alias) {
        // IllegalArgumentException will be thrown if tuple doesn't exist in
        // query
        // If this is the case then just return null
        try {
            return tuple.get(alias);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<Tuple> getResults() {
        final Collection<Tuple> results;
        if (distinct) {
            results = new HashSet<>();
        } else {
            results = new ArrayList<>();
        }

        if (builders.size() > 0) {
            for (JpaDslTupleBuilderGroupItem<E> builder : builders) {
                results.addAll(makeQuery(builder));
            }
        } else {
            results.addAll(makeQuery(null));
        }

        if (distinct) {
            this.results = new ArrayList<>(results);
        } else {
            this.results = (List<Tuple>) results;
        }

        return this.results;
    }

    private List<Tuple> makeQuery(final JpaDslTupleBuilderGroupItem<E> builder) {
        final JpaDslTupleBuilder<E> q = new JpaDslTupleBuilder<>(entityClass);

        for (Entry<SingularAttribute<E, ?>, Integer> multiselect : multiselects.entrySet()) {
            q.multiselect(multiselect.getKey());
        }

        final List<Condition<E>> conditions = new LinkedList<>();

        if (common != null) {
            common.conditionsWillBeAdded(q, conditions);
        }

        if (builder != null) {
            builder.conditionsWillBeAdded(q, conditions);
        }

        if (distinct) {
            q.distinct();
        }

        q.where(conditions);

        for (JpaDslOrder order : orders) {
            q.orderBy(order.getField(), order.getAscending());
        }

        return q.getResultList();
    }

    public void distinct() {
        distinct = true;
    }

    public void orderBy(final String field, final boolean ascending) {
        orders.add(new JpaDslOrder(field, ascending));
    }
}
