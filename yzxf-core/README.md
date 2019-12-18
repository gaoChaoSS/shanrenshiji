



insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('02AA5E6D-E5AE-4C91-8300-300F177F43B0','te',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','是否特价','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('061E7370-41C5-4E09-85D9-8D7522E7ABB8','tag',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'标签','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('0654910F-8911-4FE2-98E8-6F2A687BCC11','stockUnit',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'库存单位','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('099E5330-191B-4100-B7DA-D10579631323','oldPrice',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','double','平均底价','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('0A4E0004-303C-4160-9DE5-AEA99D293D84','stockCount',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','int','当前库存数','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('0AE5AFC9-E298-4BC4-B017-CEF2539FA098','salePrice',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','double','价格','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('1EEA7F25-B86E-4894-B4EC-9FFB9CB4A540','createTime',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','long','创建时间','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('2F15BE5E-3EC4-4716-A375-70F4DE88F8D7','typeId',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'类别Id','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('44CB278E-B255-407E-AF91-611F489FEBEE','hot',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','热卖','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('44D8032C-9D7A-492A-B695-F9575B34EFF4','hasCode',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','有无条码','商品上是否有条码');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('49324463-2A46-451B-9813-42EF0FC64D69','creator',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'创建人','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('493D78FE-9DE4-4B04-B3E5-EB2217BC1000','new',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','是否新品','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('4E8077B4-7FD0-4B89-822F-B94D819C8E36','storeId',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'店铺Id','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('51F02D6E-694F-4E54-8BDE-2F489D31DCA8','notReturn',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','不能退货','默认为false，即可以退货');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('5DBD44BF-58EA-4902-AE74-107A0D6CF8D3','spec',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'规格','用逗号隔开，以后用表关联');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('6DC3B94A-3C37-4796-B08E-73F61B7FEAFF','_id',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','string','主键','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('71600706-14AC-4761-B689-265FACFC9046','isDiscount',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','是否打折','表示商品是否参与打折，比如烟不打折');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('87BABF6D-3A2A-4FBF-A300-63FABD75FDC8','desc',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'商品描述','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('9259FA54-6EA5-40A8-91A0-4186C4D20990','icon',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','string','商品图片','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('AA18CD93-AEB8-47DC-A796-91F0425ACDE5','isNeedMake',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','是否需要制作','用于奶茶，餐饮等需要制作的商品');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('BDA9A7E1-3C55-4204-B349-38D656EE62B2','productNo',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'商品条码','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('BEE25888-F520-45F9-9DE1-2C3AA2118583','name',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','string','商品名','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('CF91E3A0-787E-4988-94D5-0EB37D468557','isDeploy',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','是否上架','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('DE51F0C2-0169-4ED3-BF00-774395A391FF','sellerId',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo',NULL,'商户Id','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('EE71DC4F-349F-4B0A-A7D8-AEB53496B634','isNeedSend',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','是否需要配送','');
insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('FC653DB5-52B1-4B50-8C2B-D3FD2983B773','notNeedStock',2,'EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','product','ProductInfo','boolean','无需管理库存','');

insert MData(_id,name,level,pid,modelName,entityName,type,title,`desc`)values('EB6CCC7F-A2DB-47DE-B906-1A8D6F4C0483','ProductInfo',1,'A609EEDE-A27B-4AC2-B3C4-7AF8A1ED3F05','product','ProductInfo','string','商品','');
