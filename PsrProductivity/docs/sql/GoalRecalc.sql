BEGIN
    DECLARE @EstimateDate TIMESTAMP 
    DECLARE @Kpi INTEGER
    DECLARE @DayToMonthEnd INTEGER
    SET @EstimateDate = '2012-09-28'
    SET @DayToMonthEnd = (SELECT COUNT(distinct cast(StartDate as date)) from TaskVisitJournal v
         where datediff(month,v.StartDate,@EstimateDate) = 0 and DATEDIFF(day,@EstimateDate,v.StartDate)>=0 and not dow(StartDate) in (1,7))    

    --����� � ��������
    SELECT Employee,Outlet 
    INTO #OutletInRoute
    FROM TaskVisitJournal v 
    WHERE datediff(day,v.StartDate,@EstimateDate) = 0 AND IsMark = 0 AND NOT Outlet IS NULL
    
    --���������� ���������� ��������� ��� ����� �� ���������
    SELECT oir.Employee,
    oir.Outlet,
    COUNT(distinct CAST(v.StartDate AS DATE)) as EstimatedVisitCount
    INTO #OutletInRouteWithEstimatedVisitCount
    FROM #OutletInRoute oir
    INNER JOIN TaskVisitJournal v ON oir.Employee=v.Employee AND oir.Outlet=v.Outlet
    WHERE v.IsMark = 0 
    AND v.EndDate IS NULL --������� ��� �� ����������
    AND DATEDIFF(day,@EstimateDate,v.StartDate)>=0 --������� � ������������ ����
    AND DATEDIFF(month,@EstimateDate,v.StartDate)=0 --�� � ���� �� ������ (������-�� ����, ��������� ����� � ������ ������ ����������� � ��������� ������
    GROUP BY oir.Employee,
    oir.Outlet
    
    DROP TABLE #OutletInRoute --��� ��� ������ �� �����
    SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='������������' AND SrcId='MV' AND IsMark = 0)
    --������������ ���� ����� �� �� �� ����
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
   

    --������� ������ ���� �� ���� �� ������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --��������� ���� �� ���� �� ������
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
    
    --������ ��������� ���� �� �� ��� ������
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
   
 --������� ������ ���� �� ���� �� �������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0

--��������� ���� �� ���� �� �������
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
  
--��������� ���� �� ��� ������ �� ����

SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='�����������������������' AND SrcId='MV' AND IsMark = 0)
    --������������ ���� ����� �� ��� �� ����
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
   

    --������� ������ ���� �� ���� �� ������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --��������� ���� �� ���� �� ������
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

 
 --������ ��������� ���� �� ��� ��� ������
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
    
   
 --������� ������ ���� �� ��� �� ���� �� �������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0

--��������� ���� �� ��� �� ���� �� �������
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
SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='���������������������' AND SrcId='MV' AND IsMark = 0)

    --������������ ���� ����� �� ��� �� ����
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
   

    --������� ������ ���� �� ���� �� ������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --��������� ���� �� ���� �� ������
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

 --������ ��������� ���� �� ��� ��� ������
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
    
   
 --������� ������ ���� �� ��� �� ���� �� �������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0

--��������� ���� �� ��� �� ���� �� �������
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

SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='�������������������CSKU' AND SrcId='MV' AND IsMark = 0)
    --������������ ���� ����� �� ��� �� ����
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
   

    --������� ������ ���� �� ���� �� ������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --��������� ���� �� ���� �� ������
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

SET @Kpi = (SELECT TOP 1 Id FROM RefKpi WHERE ExtId='����������������' AND SrcId='MV' AND IsMark = 0)
    --������������ ���� ����� �� ��� �� ����
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
   

    --������� ������ ���� �� ���� �� ������
    DELETE FROM DBA.RegKpiMatrix 
    WHERE Kpi = @Kpi
    AND Horizon = 0 
    AND NOT Outlet IS NULL
    AND DATEDIFF(month,Period,@EstimateDate)=0
    
    --��������� ���� �� ���� �� ������
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
