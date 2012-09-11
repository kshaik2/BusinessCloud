package com.infor.cloudsuite.impexp;

import java.util.HashMap;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class ImportTrackingObject {
	private boolean success=false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ImportTrackingObject() {
		
	}
	
	private final HashMap<String,Long> trackings=new HashMap<>();
	
	
	public Long get(String key) {
		Long ret=trackings.get(key);
		if (ret==null) {
			ret=0L;
		} 
		return ret;
	}
	
	public void increment(String key) {
		Long value=trackings.get(key);
		if (value == null) {
			value= 1L;
		} else {
			value++;
		}
		trackings.put(key,value);
	}
	
	public void decrement(String key) {
		Long value=trackings.get(key);
		if (value == null) {
			value=0L;
		} else {
			value--;
		}
		trackings.put(key,value);
	}
	
	public void set(String key,Long value) {
		trackings.put(key,value);
	}

	public void set(String key, int value) {
		trackings.put(key, (long) value);
	}
	public void addTo(String key, long addvalue) {
	
		Long storedval=trackings.get(key);
		if (storedval==null) {
			storedval=0L;
		}
		storedval+=addvalue;
		trackings.put(key, storedval);
		
	}
	public void setTotal(String key, String ... keys) {
		Long value=0L;
		for (String addKey : keys){
			value+=get(addKey);
		}
		trackings.put(key,value);
	}
	
	public void setDifference(String key, String keyOne,String keyTwo) {
		
		trackings.put(key,get(keyOne)-get(keyTwo));
	}
	public ImportTrackingObject reset() {
		trackings.clear();
		success=false;
		return this;
	}
	
	public JSONObject markJsonObject(JSONObject jsonObject, boolean reset) {
		if (jsonObject==null) {
			jsonObject=new JSONObject();
		}
		
		for (String key : trackings.keySet()) {
			try {
			jsonObject.put(key, trackings.get(key));
			} catch (JSONException e) {
                //ignore
			}
		}
	
		try {
			jsonObject.put("success",success);
		} catch (JSONException e) {
            //ignore
		}
		
		if (reset) {
			reset();
		}
		
		return jsonObject;
	}
	
	public JSONObject markJsonObject(JSONObject jsonObject) {
		return markJsonObject(jsonObject,false);
	}
}
