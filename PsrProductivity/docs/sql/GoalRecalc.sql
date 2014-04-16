BEGIN
    DECLARE @EstimateDate TIMESTAMP 
    DECLARE @Kpi INTEGER
    DECLARE @DayToMonthEnd INTEGER
    SET @EstimateDate = '2012-09-28'
    SET @DayToMonthEnd = (SELECT COUNT(distinct cast(StartDate as date)) from TaskVisitJournal v
         where datediff(month,v.StartDate,@EstimateDate) = 0 and DATEDIFF(day,@EstimateDate,v.StartDate)>=0 and not dow(StartDate) in (1,7))    

    --точки в маршруте
    SELECT Employee,Outlet 
    INTO #OutletInRoute
    FROM TaskVisitJournal v 
    WHERE datediff(day,v.StartDate,@EstimateDate) = 0 AND IsMark = 0 AND NOT Outlet IS NULL
    
    --количество оставшихся посещений для точек из маршрутов
    SELECT oir.Employee,
    oir.Outlet,
    COUNT(distinct CAST(v.StartDate AS DATE)) as EstimatedVisitCount
    INTO #OutletInRouteWithEstimatedVisitCount
    FROM #OutletInRoute oir
    INNER JOIN TaskVisitJournal v ON oir.Employee=v.Employee AND oir.Outlet=v.Outlet
    WHERE v.IsMark = 0 
    AND v.EndDate IS NULL --которые еще не состоялись
    AND DATEDIFF(day,@EstimateDate,v.StartDate)>=0 --начиная с интересующей даты
    AND DATEDIFF(month,@EstimateDate,v.StartDate)=0 --но в этом же месяце (вообще-то лажа, последний визит в месяце обычно отгружается в следующем месяце
    GROUP BY oir.Employee,
    oir.Outlet
    
    DROP TABLE #OutletInRoute --она нам больше не нужна
    SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='Товарооборот' AND SrcId='MV' AND IsMark = 0)
    --рассчитываем план точек по ТО на день
    SELECT oir.Employee,
    oir.Outlet,
    (COALESCE(m.Target,0)-COALESCE(m.Fact,0))/oir.EstimatedVisitCount as CalculatedTarget,
    (CASE WHEN CalculatedTarget<0 THEN 0 ELSE CalculatedTarget END) as Target,
    m.KpiMatrix
    INTO #OutletTurnoverByDay
    FROM #OutletInRouteWithEstimatedVisitCount oir 
    inner join RegKpiMatrix m 
    ON m.Horizon = 1 
    AND DATEDIFF(month,m.Period,@EstimateDate)=0 
    AND oir.Employee=m.Employee
    AND oir.Outlet=m.Outlet
    AND m.Kpi=@Kpi
   

    --удаляем старые цели на день по точкам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --добавляем цели на день по точкам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target

    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    otbd.Outlet,
    0 as Fact,
    otbd.Target 
    FROM #OutletTurnoverByDay otbd
    
    DROP TABLE #OutletTurnoverByDay
    
    --Теперь посчитаем цели по ТО для агента
    SELECT mo.Employee,
    SUM(mo.Target) as Target,
    @EstimateDate as Period
    ,MAX(me.KpiMatrix) as KpiMatrix
    INTO #TurnoverByDay
    FROM RegKpiMatrix mo
  INNER JOIN RegKpiMatrix me ON mo.Employee=me.Employee and me.Horizon = 1 and me.Outlet IS NULL and me.Kpi=@Kpi and DATEDIFF(month,me.Period,mo.Period)=0 
    WHERE mo.Horizon = 0
    AND mo.Period=@EstimateDate
    AND mo.kpi = @Kpi
    AND NOT mo.Outlet IS NULL 
    GROUP BY mo.Employee
   
 --удаляем старые цели на день по агентам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0

--добавляем цели на день по Агентам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target
    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    null as Outlet,
    0 as Fact,
    otbd.Target 
    FROM #TurnoverByDay otbd
  
--Добавляем цели по ОПД точкам на день

SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='ДистрибьюцияНакопленная' AND SrcId='MV' AND IsMark = 0)
    --рассчитываем план точек по ОПД на день
    SELECT oir.Employee,
    oir.Outlet,
    COALESCE(m.Target,0) as Target,
    COALESCE(m.Fact,0) as Fact,
    m.KpiMatrix
    INTO #OutletDistributionByDay
    FROM #OutletInRouteWithEstimatedVisitCount oir 
    inner join RegKpiMatrix m 
    ON m.Horizon = 1 
    AND DATEDIFF(month,m.Period,@EstimateDate)=0 
    AND oir.Employee=m.Employee
    AND oir.Outlet=m.Outlet
    AND m.Kpi=@Kpi
   

    --удаляем старые цели на день по точкам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --добавляем цели на день по точкам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target
    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    otbd.Outlet,
    otbd.Fact,
    otbd.Target 
    FROM #OutletDistributionByDay otbd
    
    DROP TABLE #OutletDistributionByDay     

 
 --Теперь посчитаем цели по ОПД для агента
    SELECT me.Employee,
    (CASE WHEN @DayToMonthEnd>0 THEN (me.Target-me.Fact)/@DayToMonthEnd ELSE (me.Target-me.Fact) END) as Target,
    @EstimateDate as Period
    ,me.KpiMatrix as KpiMatrix
    INTO #DistributionByDay
    FROM  RegKpiMatrix me 
    WHERE me.Horizon = 1
    and me.Outlet IS NULL 
    and me.Kpi=@Kpi 
    and DATEDIFF(month,me.Period,@EstimateDate)=0 
    
   
 --удаляем старые цели по ОПД на день по агентам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0

--добавляем цели по ОПД на день по Агентам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target
    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    null as Outlet,
    0 as Fact,
    otbd.Target 
    FROM #DistributionByDay otbd
    DROP TABLE #DistributionByDay
//***********************************************************************************************************
SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='ДистрибьюцияИнициатив' AND SrcId='MV' AND IsMark = 0)

    --рассчитываем план точек по ИПД на день
    SELECT oir.Employee,
    oir.Outlet,
    COALESCE(m.Target,0) as Target,
    COALESCE(m.Fact,0) as Fact,
    m.Initiative,
    m.KpiMatrix
    INTO #OutletDistributionByDay
    FROM #OutletInRouteWithEstimatedVisitCount oir 
    inner join RegKpiMatrix m 
    ON m.Horizon = 1 
    AND DATEDIFF(month,m.Period,@EstimateDate)=0 
    AND oir.Employee=m.Employee
    AND oir.Outlet=m.Outlet
    AND m.Kpi=@Kpi
   

    --удаляем старые цели на день по точкам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --добавляем цели на день по точкам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Initiative,
    Fact,
    Target
    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    otbd.Outlet,
    otbd.Initiative,
    otbd.Fact,
    otbd.Target 
    FROM #OutletDistributionByDay otbd
    
    DROP TABLE #OutletDistributionByDay  

 --Теперь посчитаем цели по ИПД для агента
    SELECT me.Employee,
    (CASE WHEN @DayToMonthEnd>0 THEN (me.Target-me.Fact)/@DayToMonthEnd ELSE (me.Target-me.Fact) END) as Target,
    @EstimateDate as Period
    ,me.KpiMatrix as KpiMatrix
    ,me.Initiative
    INTO #DistributionByDay
    FROM  RegKpiMatrix me 
    WHERE me.Horizon = 1
    and me.Outlet IS NULL 
    and me.Kpi=@Kpi 
    and DATEDIFF(month,me.Period,@EstimateDate)=0 
    
   
 --удаляем старые цели по ОПД на день по агентам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0

--добавляем цели по ИПД на день по Агентам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target,
    Initiative

    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    null as Outlet,
    0 as Fact,
    otbd.Target,
    otbd.Initiative 
    FROM #DistributionByDay otbd  
//***********************************************************************************************************

SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='ДистрибьюцияЗолотыхCSKU' AND SrcId='MV' AND IsMark = 0)
    --рассчитываем план точек по ЗПД на день
    SELECT oir.Employee,
    oir.Outlet,
    COALESCE(m.Target,0) as Target,
    COALESCE(m.Fact,0) as Fact,
    m.KpiMatrix
    INTO #OutletDistributionByDay
    FROM #OutletInRouteWithEstimatedVisitCount oir 
    inner join RegKpiMatrix m 
    ON m.Horizon = 1 
    AND DATEDIFF(month,m.Period,@EstimateDate)=0 
    AND oir.Employee=m.Employee
    AND oir.Outlet=m.Outlet
    AND m.Kpi=@Kpi
   

    --удаляем старые цели на день по точкам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --добавляем цели на день по точкам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target
    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    otbd.Outlet,
    otbd.Fact,
    otbd.Target 
    FROM #OutletDistributionByDay otbd
    
    DROP TABLE #OutletDistributionByDay   

SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='Представленность' AND SrcId='MV' AND IsMark = 0)
    --рассчитываем план точек по ЗПД на день
    SELECT oir.Employee,
    oir.Outlet,
    COALESCE(m.Target,0) as Target,
    COALESCE(m.Fact,0) as Fact,
    m.KpiMatrix
    INTO #OutletDistributionByDay
    FROM #OutletInRouteWithEstimatedVisitCount oir 
    inner join RegKpiMatrix m 
    ON m.Horizon = 1 
    AND DATEDIFF(month,m.Period,@EstimateDate)=0 
    AND oir.Employee=m.Employee
    AND oir.Outlet=m.Outlet
    AND m.Kpi=@Kpi
   

    --удаляем старые цели на день по точкам
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --добавляем цели на день по точкам
    INSERT INTO RegKpiMatrix(
    Horizon,
    Period,
    Kpi,
    KpiMatrix,
    Employee,
    Outlet,
    Fact,
    Target
    )
    SELECT 0 as Horizon,
    @EstimateDate as Period,
    @Kpi as Kpi,
    otbd.KpiMatrix,
    otbd.Employee,
    otbd.Outlet,
    otbd.Fact,
    otbd.Target 
    FROM #OutletDistributionByDay otbd
    
    DROP TABLE #OutletDistributionByDay   

END
--select  * from refuser
--select * from TaskVisitJournal where Employee=841 and Outlet=6514

--SELECT DATEDIFF(month,'2012-09-30','2012-10-01')

--select * from RegKpiMatrix where kpi=142
