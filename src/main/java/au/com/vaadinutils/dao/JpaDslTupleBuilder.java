package au.com.vaadinutils.dao;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;

public class JpaDslTupleBuilder<E> extends JpaDslAbstract<E, Tuple> {
    private List<Selection<?>> multiselects = new LinkedList<>();

    public JpaDslTupleBuilder(Class<E> entityClass) {
        this.entityClass = entityClass;
        builder = getEntityManager().getCriteriaBuilder();

        criteria = builder.createTupleQuery();
        root = criteria.from(entityClass);
    }

    public <T> Path<T> multiselect(final SingularAttribute<E, T> attribute) {
        final Path<T> path = root.get(attribute);
        multiselects.add(path);
        return path;
    }

    public <T> Path<T> multiselect(final Path<T> path) {
        multiselects.add(path);
        return path;
    }

    public <J, T> Path<T> multiselect(final JoinBuilder<E, J> join, final SingularAttribute<J, T> attribute) {
        final Path<T> path = getJoin(join).get(attribute);
        multiselects.add(path);
        return path;
    }

    public <J, T> Selection<T> multiselect(final JoinBuilder<E, J> join, final SingularAttribute<J, T> attribute,
            final String alias) {
        final Selection<T> selection = getJoin(join).get(attribute).alias(alias);
        multiselects.add(selection);

        return selection;
    }

    public JpaDslTupleBuilder<E> multiselect(final Selection<?> selection) {
        multiselects.add(selection);
        return this;
    }

    @Override
    public List<Tuple> getResultList() {
        criteria.multiselect(multiselects);
        return super.getResultList();
    }

    @Override
    public Tuple getSingleResult() {
        criteria.multiselect(multiselects);
        return super.getSingleResult();
    }

    @Override
    public Tuple getSingleResultOrNull() {
        criteria.multiselect(multiselects);
        return super.getSingleResultOrNull();
    }
}
