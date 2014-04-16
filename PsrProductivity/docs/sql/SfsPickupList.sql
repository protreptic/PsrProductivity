SELECT ra.Id as Id
, i.id as prodItem
, p.Price as prodPrice
, COALESCE(s.ABC,0) as abc
, COALESCE(s.IsPriority,0) as isPriority
, COALESCE(s.IsDrive,0) as isDrive
, 0 as isRecommended
, COALESCE(w.Quantity,0) as Stock
 FROM RegAssortment ra
 INNER JOIN RefProductItem i on ra.ProductItem=i.Id
 INNER JOIN RegPrice p on i.Id = p.ProductItem
 INNER JOIN RefCsku c on i.ParentExt=c.Id
 LEFT JOIN RegCskuState s on c.Id=s.Csku
 and s.StoreChannel=18 
 LEFT JOIN RegOrderHelper h on c.Id=s.Csku
 and h.Employee=839
and h.Outlet=2781 
 LEFT JOIN RegStock w on ra.ProductItem = w.ProductItem and w.Warehouse=3 
 WHERE 
-- ra.Assortment=72 
-- AND
 p.PriceType=1060 
 AND c.Fullpath LIKE '%' 
 AND s.ABC IS NULL 
 AND h.Id IS NULL 
 AND NOT NULLIF(p.Price,0) IS NULL 
 ORDER BY c.OrderKey 
--select * from RefOutlet