package com.musinsa.point.domain.repository.pointUsageLink;

import java.util.Collection;
import java.util.Map;

public interface PointUsageLinkRepositoryCustom {
    Map<Long, Long> sumByPointItemKey(Collection<Long> pointItemList);
}
