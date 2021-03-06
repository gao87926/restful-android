package mn.aug.restfulandroid.service;

import mn.aug.restfulandroid.provider.ProfileConstants;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Profile;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;


class ProfileProcessor {

	protected static final String TAG = ProfileProcessor.class.getSimpleName();

	private Context mContext;

	public ProfileProcessor(Context context) {
		mContext = context;
	}

	void getProfile(ProfileProcessorCallback callback) {

		// (4) Insert-Update the ContentProvider with a status column and
		// results column
		// Look at ContentProvider example, and build a content provider
		// that tracks the necessary data.

		// (5) Call the REST method
		// Create a RESTMethod class that knows how to assemble the URL,
		// and performs the HTTP operation.

		@SuppressWarnings("unchecked")
		RestMethod<Profile> getProfileMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
				ProfileConstants.CONTENT_URI, Method.GET, null, null);
		RestMethodResult<Profile> result = getProfileMethod.execute();

		/*
		 * (8) Insert-Update the ContentProvider status, and insert the result
		 * on success Parsing the JSON response (on success) and inserting into
		 * the content provider
		 */

		updateContentProvider(result);

		// (9) Operation complete callback to Service

		callback.send(result.getStatusCode());

	}

	private void updateContentProvider(RestMethodResult<Profile> result) {

		if (result != null && result.getResource() != null) {

			String name = result.getResource().getName();

			if (name != null) {

				ContentValues values = new ContentValues();
				values.put(ProfileConstants.NAME, name);

				Cursor cursor = mContext.getContentResolver().query(ProfileConstants.CONTENT_URI,
						null, null, null, null);
				if (cursor.moveToFirst()) {
					int id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
					mContext.getContentResolver().update(
							ContentUris.withAppendedId(ProfileConstants.CONTENT_URI, id), values,
							null, null);
				} else {
					mContext.getContentResolver().insert(ProfileConstants.CONTENT_URI, values);
				}
				cursor.close();
			}
		}

	}

}
