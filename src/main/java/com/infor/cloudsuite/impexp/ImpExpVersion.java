package com.infor.cloudsuite.impexp;

public enum ImpExpVersion {
	VERSION_ONE(com.infor.cloudsuite.impexp.VersionOneImporter.class),
	VERSION_TWO(com.infor.cloudsuite.impexp.VersionTwoImporter.class),
	VERSION_TWO_SEED(com.infor.cloudsuite.impexp.VersionTwoSeeder.class),
	DUMMY(com.infor.cloudsuite.impexp.DummyImporter.class);
	

	private Class<?extends com.infor.cloudsuite.impexp.AbstractImporter> importerClass;
	
	ImpExpVersion(Class<?extends com.infor.cloudsuite.impexp.AbstractImporter> importerClass) {
		this.importerClass=importerClass;
	}
	
	
	public String toString() {
	
		return this.name();
	}
	
	public Class<?extends com.infor.cloudsuite.impexp.AbstractImporter> getImporterClass() {
		return importerClass;
	}
	
}
