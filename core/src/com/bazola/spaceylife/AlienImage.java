package com.bazola.spaceylife;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bazola.spaceylife.gamemodel.Alien;

public class AlienImage extends Image {
	
	private final Alien alien;
	
	private Animation animation;
	private float stateTime = 0;

	public AlienImage(Texture texture, Alien alien) {
		super(texture);
		this.alien = alien;
		this.update();
	}
	
	public void update() {
		this.setPosition(alien.getPosition().x, alien.getPosition().y);
	}
	
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
    @Override
    public void act(float delta) {
    	super.act(delta);
    	if (this.animation == null) {
    		return;
    	}
        ((TextureRegionDrawable)getDrawable()).setRegion(animation.getKeyFrame(stateTime+=delta, true));
    }
}
