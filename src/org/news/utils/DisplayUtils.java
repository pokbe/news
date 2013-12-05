package org.news.utils;

import android.content.Context;

public class DisplayUtils {
	
	/**��pxת����dip**/
	public int pxToDip(Context context, int pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue * scale + 0.5f);
	}

}
