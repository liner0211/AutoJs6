/**
 *  Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package zhao.arsceditor.ResDecoder.data.value;

import android.util.TypedValue;

import java.io.IOException;

import zhao.arsceditor.ResDecoder.IO.Duo;
import zhao.arsceditor.ResDecoder.data.ResPackage;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public class ResValueFactory {
	private final ResPackage mPackage;

	public ResValueFactory(ResPackage pakage_) {
		mPackage = pakage_;
	}

	public ResBagValue bagFactory(int parent, Duo<Integer, ResScalarValue>[] items) throws IOException {
		ResReferenceValue parentVal = newReference(parent, null);

		if (items.length == 0) {
			return new ResBagValue(parentVal);
		}
		int key = items[0].m1;
		if (key == ResAttr.BAG_KEY_ATTR_TYPE) {
			return ResAttr.factory(parentVal, items, this, mPackage);
		}
		if (key == ResArrayValue.BAG_KEY_ARRAY_START) {
			return new ResArrayValue(parentVal, items);
		}
		return new ResStyleValue(parentVal, items, this);
	}

	public ResScalarValue factory(int type, int value, String rawValue) throws IOException {
		switch (type) {
		case TypedValue.TYPE_NULL:
			return new ResReferenceValue(mPackage, 0, null);
		case TypedValue.TYPE_REFERENCE:
			return newReference(value, rawValue);
		case TypedValue.TYPE_ATTRIBUTE:
			return newReference(value, rawValue, true);
		case TypedValue.TYPE_STRING:
			return new ResStringValue(rawValue, value);
		case TypedValue.TYPE_FLOAT:
			return new ResFloatValue(Float.intBitsToFloat(value), value, rawValue);
		case TypedValue.TYPE_DIMENSION:
			return new ResDimenValue(value, rawValue);
		case TypedValue.TYPE_FRACTION:
			return new ResFractionValue(value, rawValue);
		case TypedValue.TYPE_INT_BOOLEAN:
			return new ResBoolValue(value != 0, value, rawValue);
		case 0x07:
			return newReference(value, rawValue);
		}

		if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return new ResColorValue(value, rawValue);
		}
		if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
			return new ResIntValue(value, rawValue, type);
		}

		throw new IOException("Invalid value type: " + type);
	}

	public ResIntBasedValue factory(String value, int rawValue) {
		if (value.startsWith("res/")) {
			return new ResFileValue(value, rawValue);
		}
		return new ResStringValue(value, rawValue);
	}

	public ResReferenceValue newReference(int resID, String rawValue) {
		return newReference(resID, rawValue, false);
	}

	public ResReferenceValue newReference(int resID, String rawValue, boolean theme) {
		return new ResReferenceValue(mPackage, resID, rawValue, theme);
	}
}