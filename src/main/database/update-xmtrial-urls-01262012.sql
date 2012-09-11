UPDATE TrialEnvironment te 
   JOIN Product p ON p.id=te.product_id 
   SET te.url=replace(te.url,'logindisp\?','login\.jsp\?') 
   WHERE p.shortName='XM';

 

UPDATE TrialInstance ti 
   JOIN Product p ON p.id=ti.product_id 
   SET ti.url=replace(ti.url,'logindisp\?','login\.jsp\?') 
   WHERE p.shortName='XM'; 
