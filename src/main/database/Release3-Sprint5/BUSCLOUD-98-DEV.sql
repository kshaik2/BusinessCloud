use businesscloud_dev;

UPDATE TrialInstance ti, ProductVersion pv SET ti.productversion_id = pv.id WHERE pv.product_id=ti.product_id ;
UPDATE TrialEnvironment te, ProductVersion pv  SET te.productversion_id = pv.id WHERE pv.product_id=te.product_id ;
INSERT INTO DeploymentStack_ProductVersion SELECT dsp.DeploymentStack_id, pv.id FROM DeploymentStack_Product dsp JOIN ProductVersion pv ON pv.product_id = dsp.deployedProducts_id ;
UPDATE TrialProductChild tpc, ProductVersion pv SET tpc.parentVersion_id=pv.id WHERE tpc.parent_id=pv.product_id;
UPDATE TrialProductChild tpc, ProductVersion pv SET tpc.childVersion_id=pv.id WHERE tpc.child_id=pv.product_id;
UPDATE AmiDescriptor_SEQ SET next_val = (SELECT max(next_val) FROM AwsProductAmiDesc_SEQ);