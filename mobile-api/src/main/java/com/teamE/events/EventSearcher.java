package com.teamE.events;

import com.teamE.commonAddsEvents.Scope;
import com.teamE.events.data.entity.Event;
import com.teamE.users.StudentHouse;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.MustJunction;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Configuration
public class EventSearcher {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Event> searchEvent(Scope scope, StudentHouse studentHouse, String query, Pageable page) {

        Query keywordQuery;
        QueryBuilder queryBuilder = getQueryBuilder();

        BooleanJunction<BooleanJunction> booleanJunctionBooleanJunction = queryBuilder
                .bool();

        List<Query> queries = new ArrayList<>();

        String text = query.trim();
        if (text.length() > 0) {
            queries.add(checkQuery(text));
        }


        MustJunction mustJunction = queryBuilder
                .bool()
                .must(checkQuery(query))
                .must(queryBuilder.keyword()
                        .onField("scope")
                        .matching(scope)
                        .createQuery());

        if (scope != Scope.DORMITORY) {
            keywordQuery = mustJunction.must(queryBuilder.keyword()
                    .onField("scope")
                    .matching(scope)
                    .createQuery()).createQuery();
        }

        if (studentHouse != null) {
            keywordQuery = mustJunction.must(queryBuilder.keyword()
                    .onField("studentHouse")
                    .matching(studentHouse)
                    .createQuery()).createQuery();
        } else {
            keywordQuery = mustJunction.createQuery();
        }

        List<Event> events = getJpaQuery(keywordQuery, page).getResultList();

        long total = getJpaQuery(keywordQuery, page).getResultSize();
        return new PageImpl<>(events, page, total);
    }


    Query checkQuery(String query) {
        QueryBuilder queryBuilder = getQueryBuilder();
        return queryBuilder
                .bool()
                .must(queryBuilder
                        .bool().should(queryBuilder.phrase()
                                .withSlop(1).onField("name")
                                .sentence(query).createQuery())
                        .should(queryBuilder.phrase()
                                .withSlop(1).onField("street")
                                .sentence(query).createQuery())
                        .should(queryBuilder.phrase()
                                .withSlop(1).onField("description")
                                .sentence(query).createQuery())
                        .should(queryBuilder.phrase()
                                .withSlop(1).onField("city")
                                .sentence(query).createQuery()).createQuery()).createQuery();
    }

    Query checkScope(Scope scope) {
        QueryBuilder queryBuilder = getQueryBuilder();

        if (scope == Scope.OTHER) {
            return queryBuilder
                    .bool().must(queryBuilder.keyword()
                            .onField("scope")
                            .matching(Scope.OTHER)
                            .createQuery()).createQuery();
        }
        if (scope == Scope.STUDENT) {
            return queryBuilder
                    .bool().must(queryBuilder.bool()
                            .should(queryBuilder.keyword()
                                    .onField("scope")
                                    .matching(Scope.OTHER).createQuery())
                            .should(queryBuilder.keyword()
                                    .onField("scope")
                                    .matching(Scope.STUDENT).createQuery())
                            .createQuery()).createQuery();
        }
        return queryBuilder
                .bool().must(queryBuilder.bool()
                        .should(queryBuilder.keyword()
                                .onField("scope")
                                .matching(Scope.OTHER).createQuery())
                        .should(queryBuilder.keyword()
                                .onField("scope")
                                .matching(Scope.STUDENT).createQuery())
                        .should(queryBuilder.keyword()
                                .onField("scope")
                                .matching(Scope.DORMITORY).createQuery())
                        .createQuery()).createQuery();
    }


    private FullTextQuery getJpaQuery(org.apache.lucene.search.Query luceneQuery, Pageable page) {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class);

        fullTextQuery.setFirstResult((int) page.getOffset());
        fullTextQuery.setMaxResults(page.getPageSize());
        return fullTextQuery;
    }

    private QueryBuilder getQueryBuilder() {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        return fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Event.class)
                .get();
    }
}
