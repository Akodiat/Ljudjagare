package at.technikum.mti.fancycoverflow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import se.chalmers.group42.runforlife.ModeController.Mode;
import se.chalmers.group42.runforlife.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FancyCoverFlowCustomAdapter extends FancyCoverFlowAdapter {

	/**
	 * Map containing an image for each game mode.
	 */
	private static final Map<Integer, Mode> images;
	static {
		images = new LinkedHashMap<Integer, Mode>();
		images.put(R.drawable.gamemodesample, Mode.COIN_COLLECTOR);
		images.put(R.drawable.gamemodesample, Mode.QUEST);
		images.put(R.drawable.gamemodesample, Mode.MONSTER_HUNT);
	}

	/**
	 * Height for the image to displayed with.
	 */
	private int height;

	public FancyCoverFlowCustomAdapter(int height) {
		this.height = height;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entry<Integer, Mode> getItem(int i) {
		return (Entry<Integer, Mode>) images.entrySet().toArray()[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getCoverFlowItem(int position, View reusableView,
			ViewGroup parent) {
		ImageView imageView = null;

		if (reusableView != null)
			imageView = (ImageView) reusableView;
		else {
			imageView = new ImageView(parent.getContext());
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(height,
					height));

		}
		getItem(position);

		imageView.setImageResource(this.getItem(position).getKey());
		return imageView;
	}

}
