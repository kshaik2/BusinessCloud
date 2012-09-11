package com.infor.cloudsuite.impexp;

import java.util.ArrayList;

import com.amazonaws.util.json.JSONObject;



public class DummyImporter extends AbstractImporter {


	private ArrayList<String> opKeyList=null;
	
	private enum OpKeyEnum {
		DUMMY_ONE,
		DUMMY_TWO,
		DUMMY_THREE,
		AND_SO_ON
    }

	@Override
	public boolean runImport(String key, JSONObject buffer) {
		JSONObject jobj=new JSONObject();
		
		addToJsonAndIgnore(jobj,"result","ran dummy mode--called with opkey:"+key);
		addToJsonAndIgnore(jobj,"success",true);
		addToJsonAndIgnore(buffer, key, jobj);
		
		return true;
	}

	@Override
	public ArrayList<String> getOpKeyList() {

		if (this.opKeyList==null) {
			this.opKeyList=new ArrayList<>();
			for (OpKeyEnum key : OpKeyEnum.values()) {
				this.opKeyList.add(key.name());
			}
		}
		
		
		return this.opKeyList;
	}

}
