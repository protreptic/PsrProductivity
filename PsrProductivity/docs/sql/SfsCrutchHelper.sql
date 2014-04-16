begin
select SrcId,Employee,Outlet,Csku,CEIL(Avg(Quantity)) as Quantity, cast(MAX(Id) as NVarchar(50)) as ExtId 
into #CrutchHelper
from RegSales
group by SrcId, Employee,Outlet,Csku;
delete from RegOrderHelper from RegOrderHelper h left join #CrutchHelper c on h.ExtId=c.ExtId and h.SrcId=c.SrcId;
update RegOrderHelper
set Quantity = c.Quantity
,LastModified=current_timestamp
from RegOrderHelper h inner join #CrutchHelper c on h.ExtId=c.ExtId and h.SrcId=c.SrcId
where c.Quantity<>h.Quantity;
insert into RegOrderHelper(ExtId,SrcId,Employee,Outlet,CSKU,Quantity)
select c.ExtId,c.SrcId,c.Employee,c.Outlet,c.CSKU,c.Quantity
from RegOrderHelper h right join #CrutchHelper c on h.ExtId=c.ExtId and h.SrcId=c.SrcId
where h.SrcId IS NULL
end;