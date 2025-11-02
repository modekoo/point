package com.musinsa.point.domain.repository.pointUsageLink;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class PointUsageLinkRepositoryImpl implements PointUsageLinkRepositoryCustom{

//    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<Long, Long> sumByPointItemKey(Collection<Long> pointItemList) {


        return Map.of();
    }
}
