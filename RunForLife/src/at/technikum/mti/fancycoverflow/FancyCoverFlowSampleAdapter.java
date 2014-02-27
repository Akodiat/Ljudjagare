/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    Modified 2014 Anton Palmqvist
 *    Taking in a parameter of a size to set for the imageview.
 */

package at.technikum.mti.fancycoverflow;

import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.R.drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class FancyCoverFlowSampleAdapter extends FancyCoverFlowAdapter {

    // =============================================================================
    // Private members
    // =============================================================================

    private int[] images = {R.drawable.gamemodesample, R.drawable.gamemodesample, R.drawable.gamemodesample, R.drawable.gamemodesample, R.drawable.gamemodesample, R.drawable.gamemodesample, R.drawable.gamemodesample};
    private int height;
    
    // =============================================================================
    // Supertype overrides
    // =============================================================================

    public FancyCoverFlowSampleAdapter(int height){
    	this.height = height;
    }
    
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Integer getItem(int i) {
        return images[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
        ImageView imageView = null;

        if (reuseableView != null) {
            imageView = (ImageView) reuseableView;
        } else {
            imageView = new ImageView(viewGroup.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(height, height));

        }

        imageView.setImageResource(this.getItem(i));
        return imageView;
    }
}
