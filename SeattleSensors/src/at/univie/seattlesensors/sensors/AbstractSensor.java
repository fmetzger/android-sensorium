/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   android-seattle-sensors is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   android-seattle-sensors is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with android-seattle-sensors. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.seattlesensors.sensors;

import java.util.List;

import android.content.Context;

public abstract class AbstractSensor {
	
	protected Context context;
	protected boolean enabled = false;
	
	public AbstractSensor(Context context){
		this.context = context;
	}
	
	public abstract void enable();
	
	public abstract void disable();
	
	public abstract boolean hasMethod(String methodname);
	
	public abstract Object[] methodSignature(String methodname);
	
	public abstract Object[] callMethod(String methodname);
	
	public boolean isEnabled(){
		return enabled;
	}

}
