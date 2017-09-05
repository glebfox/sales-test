alter table SALES_ORDER drop column DATE_ cascade ;
alter table SALES_ORDER add column DATE_ date ^
update SALES_ORDER set DATE_ = current_date where DATE_ is null ;
alter table SALES_ORDER alter column DATE_ set not null ;
