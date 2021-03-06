package com.bazola.spaceylife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bazola.spaceylife.audio.MusicPlayer;
import com.bazola.spaceylife.audio.MusicType;
import com.bazola.spaceylife.audio.SoundPlayer;
import com.bazola.spaceylife.audio.SoundType;
import com.bazola.spaceylife.camera.CameraPanner;
import com.bazola.spaceylife.camera.PinchZoomer;
import com.bazola.spaceylife.camera.ScrollWheelZoomer;
import com.bazola.spaceylife.uielements.ShipType;
import com.bazola.spaceylife.uielements.StarType;

public class LibGDXGame extends Game {
	
	private final Random random = new Random();
	
	public static float STAGE_WIDTH;
	public static float STAGE_HEIGHT;
	
	public static float HUD_WIDTH;
	public static float HUD_HEIGHT;
	
	public InputMultiplexer inputHandler = new InputMultiplexer();
	public CameraPanner cameraPanner;
	public PinchZoomer pinchZoomer;
	public ScrollWheelZoomer scrollWheelZoomer;
	
	public OrthographicCamera backgroundCamera;
	public SpriteBatch backgroundBatch;
	public Stage backgroundStage;

	public ParallaxCamera parallaxCamera;
	public SpriteBatch parallaxBatch;
	public ParallaxDrawer parallaxDrawer;
	
	public OrthographicCamera camera;
	public Stage stage;
	public SpriteBatch batch;
	
	public Stage fogStage;
	public SpriteBatch fogBatch;
	
	public OrthographicCamera hudCamera;
	public SpriteBatch hudBatch;
	public Stage hudStage;
	
	public Map<StarType, TextureRegion> starTextures;
	public Map<ShipType, TextureRegion> shipTextures;
	
	public Texture gridBackground;
	public Texture radarRing01;
	public Texture alien01;
	public Texture aiPlanetCover01;
	public Texture playerPlanetCover01;
	public Texture laser01;
	public Texture blackCircle;
	public Texture nebula02;
	public Texture nebula03;
	public Texture nebula04;
	public Texture nebula05;
	public Texture screenOverlay01;
	public Texture seamlessSpace;
	public Texture seamlessSpaceTrans;

	public Skin skin;

	public NinePatch menuBackgroundTransparent;
	public NinePatch menuBackgroundSolid;
	public NinePatch menuBackgroundDark;
	
	private GameScreen gameScreen;
	private MenuScreen menuScreen;
	
	public Animation alienMove01;
	public Animation alienEat01;
	public Animation flagWave01;
	
    public Animation angry01Animation;
    public Animation hello01Animation;
    public Animation idle01Animation;
    public Animation idle02Animation;
    public Animation screenBackgroundAnimation;
    
	public Animation bigAlienMove01;
	public Animation bigAlienEat01;
	
	public Animation eye01Animation;
	public Animation station01Animation;
    
    private Map<String,Map<Integer,BitmapFont>> fontsShadow;
    
    public BitmapFont titleFont;
    public BitmapFont bigButtonFont;
    public BitmapFont smallButtonFont;
	
	/**
	 * Need to keep a reference to the atlases for the sake of disposing them
	 * This is better than trying to grab a TextureRegion and dispose of the texture
	 */
	private TextureAtlas starAtlasForDispose;
	private TextureAtlas shipAtlasForDispose;
	
	private TextureAtlas angry01Atlas;
	private TextureAtlas hello01Atlas;
	private TextureAtlas idle01Atlas;
	private TextureAtlas idle02Atlas;
	private TextureAtlas screenBackgroundAtlas;

	private TextureAtlas bigAlien01Atlas;
	private TextureAtlas eye01Atlas;
	private TextureAtlas station01Atlas;
	
	public Map<MusicType, Music> music;
	public Map<SoundType, Sound> sounds;
	public MusicPlayer musicPlayer;
	public SoundPlayer soundPlayer;
	
	@Override
	public void create () {
		
        LibGDXGame.STAGE_WIDTH = Gdx.graphics.getWidth();
        LibGDXGame.STAGE_HEIGHT = Gdx.graphics.getHeight();
        
        LibGDXGame.HUD_WIDTH = 600;
        LibGDXGame.HUD_HEIGHT = 600;
        
        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	
		this.loadResources();
		
        this.backgroundCamera = new OrthographicCamera(HUD_WIDTH, HUD_WIDTH);
        this.backgroundCamera.setToOrtho(false, HUD_WIDTH, HUD_WIDTH);
        this.backgroundBatch = new SpriteBatch();
        this.backgroundStage = new Stage(new StretchViewport(HUD_WIDTH, HUD_WIDTH, backgroundCamera), backgroundBatch);

        this.parallaxCamera = new ParallaxCamera(HUD_WIDTH, HUD_HEIGHT);
        this.parallaxBatch = new SpriteBatch();
        this.parallaxDrawer = new ParallaxDrawer(this.random,
        										 this.parallaxBatch, 
        									     this.seamlessSpace, 
        									     this.seamlessSpaceTrans, 
        									     4500, //hard coded world sizes here
        									     3000,
        									     (int)LibGDXGame.HUD_WIDTH, 
        									     (int)LibGDXGame.HUD_HEIGHT);
        
        this.camera = new OrthographicCamera(STAGE_WIDTH, STAGE_HEIGHT);
        this.camera.setToOrtho(false, STAGE_WIDTH, STAGE_HEIGHT);
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(camera), batch);
        
        this.fogBatch = new SpriteBatch();
        this.fogStage = new Stage(new ScreenViewport(camera), fogBatch);
        //this.fogStage.setDebugAll(true);
        
        this.hudCamera = new OrthographicCamera(HUD_WIDTH, HUD_HEIGHT);
        this.hudCamera.setToOrtho(false, HUD_WIDTH, HUD_HEIGHT);
        this.hudBatch = new SpriteBatch();
        this.hudStage = new Stage(new StretchViewport(HUD_WIDTH, HUD_HEIGHT, hudCamera), hudBatch);
	
		this.configureInputHandlers();
		
		this.menuScreen = new MenuScreen(this, this.random);
		this.setScreen(this.menuScreen);
	}
	
	public void clickedPlayButton() {
		this.camera.zoom = 1;
		this.parallaxCamera.position.x = 0;
		this.parallaxCamera.position.y = 0;
        this.gameScreen = new GameScreen(this, this.random);
        this.setScreen(this.gameScreen);
	}
	
	public void exitToMainMenu() {
		this.backgroundStage.clear();
		this.stage.clear();
		this.fogStage.clear();
		this.hudStage.clear();
		
		this.menuScreen = new MenuScreen(this, this.random);
		this.setScreen(this.menuScreen);
	}
	
	private void loadResources() {
		
        Texture patchTextureTransparent = new Texture("green9patchTransparent.png");
        int width = patchTextureTransparent.getWidth();
        int height = patchTextureTransparent.getHeight();
        this.menuBackgroundTransparent = new NinePatch(patchTextureTransparent, width/2 - 5, width/2 - 5, 
        													         		    height/2 - 5, height/2 - 5);
		
        Texture patchTexture = new Texture("green9patch.png");
        width = patchTexture.getWidth();
        height = patchTexture.getHeight();
        this.menuBackgroundSolid = new NinePatch(patchTexture, width/2 - 5, width/2 - 5, 
        													   height/2 - 5, height/2 - 5);
        
        Texture patchTextureDark = new Texture("green9patchDark.png");
        width = patchTextureDark.getWidth();
        height = patchTextureDark.getHeight();
        this.menuBackgroundDark = new NinePatch(patchTextureDark, width/2 - 5, width/2 - 5, 
        													      height/2 - 5, height/2 - 5);
        
		this.gridBackground = new Texture("Grid3.png");
		
		this.radarRing01 = new Texture("radarCircle02_white.png");
		
		this.alien01 = new Texture("baseSlimeEyes.png");
		
		this.aiPlanetCover01 = new Texture("aiStation02.png");
		this.playerPlanetCover01 = new Texture("alienCovering01.png");
		
		this.laser01 = new Texture("purpleShot04.png");
		
		this.blackCircle = new Texture("blackCircle.png");
		
		this.nebula02 = new Texture("nebula02.png");
		this.nebula03 = new Texture("nebula03.png");
		this.nebula04 = new Texture("nebula04.png");
		this.nebula05 = new Texture("nebula05.png");
		
    	this.screenOverlay01 = new Texture("screenFront.png");
    	
    	this.seamlessSpace = new Texture("seamlessSpace.png");
    	this.seamlessSpaceTrans = new Texture("seamlessSpaceTrans.png");
		
		this.starTextures = this.loadStarTextures();
		this.shipTextures = this.loadShipTextures();
		
		this.loadAnimations();
		
		this.fontsShadow = this.loadFonts(true);
		this.configureFonts();
		
		this.music = this.loadMusic();
		this.sounds = this.loadSounds();
		this.musicPlayer = new MusicPlayer(this, this.random);
		this.soundPlayer = new SoundPlayer(this);
	}
	
    private void loadAnimations() {
		Texture alienMove01 = new Texture("slime_moving_01.png");
		Texture alienMove02 = new Texture("slime_moving_02.png");
		Texture alienMove03 = new Texture("slime_moving_03.png");
		TextureRegion[] regions = new TextureRegion[4];
		regions[0] = new TextureRegion(alienMove01);
		regions[1] = new TextureRegion(alienMove02);
		regions[2] = new TextureRegion(alienMove03);
		regions[3] = new TextureRegion(alienMove02);
		this.alienMove01 = new Animation(1/6f, regions);
		
		Texture alienEat01 = new Texture("slime_eat_01.png");
		Texture alienEat02 = new Texture("slime_eat_02.png");
		Texture alienEat03 = new Texture("slime_eat_03.png");
		Texture alienEat04 = new Texture("slime_eat_04.png");
		Texture alienEat05 = new Texture("slime_eat_05.png");
		Texture alienEat06 = new Texture("slime_eat_06.png");
		Texture alienEat07 = new Texture("slime_eat_07.png");
		Texture alienEat08 = new Texture("slime_eat_08.png");
		TextureRegion[] eatRegions = new TextureRegion[13];
		eatRegions[0] = new TextureRegion(alienEat01);
		eatRegions[1] = new TextureRegion(alienEat02);
		eatRegions[2] = new TextureRegion(alienEat03);
		eatRegions[3] = new TextureRegion(alienEat04);
		eatRegions[4] = new TextureRegion(alienEat05);
		eatRegions[5] = new TextureRegion(alienEat06);
		eatRegions[6] = new TextureRegion(alienEat07);
		eatRegions[7] = new TextureRegion(alienEat08);
		eatRegions[8] = new TextureRegion(alienEat07);
		eatRegions[9] = new TextureRegion(alienEat06);
		eatRegions[10] = new TextureRegion(alienEat05);
		eatRegions[11] = new TextureRegion(alienEat04);
		eatRegions[12] = new TextureRegion(alienEat03);
		this.alienEat01 = new Animation(1/6f, eatRegions);
		
		Texture flagWave01 = new Texture("redFlag01.png");
		Texture flagWave02 = new Texture("redFlag02.png");
		Texture flagWave03 = new Texture("redFlag03.png");
		Texture flagWave04 = new Texture("redFlag04.png");
		TextureRegion[] flagRegions = new TextureRegion[4];
		flagRegions[0] = new TextureRegion(flagWave01);
		flagRegions[1] = new TextureRegion(flagWave02);
		flagRegions[2] = new TextureRegion(flagWave03);
		flagRegions[3] = new TextureRegion(flagWave04);
		this.flagWave01 = new Animation(1/6f, flagRegions);
		
		this.bigAlien01Atlas = new TextureAtlas(Gdx.files.internal("bigAlien01.atlas"));
		TextureRegion[] bigEatRegions = new TextureRegion[13];
		bigEatRegions[0] = this.bigAlien01Atlas.findRegion("big_slime_eat01");
		bigEatRegions[1] = this.bigAlien01Atlas.findRegion("big_slime_eat02");
		bigEatRegions[2] = this.bigAlien01Atlas.findRegion("big_slime_eat03");
		bigEatRegions[3] = this.bigAlien01Atlas.findRegion("big_slime_eat04");
		bigEatRegions[4] = this.bigAlien01Atlas.findRegion("big_slime_eat05");
		bigEatRegions[5] = this.bigAlien01Atlas.findRegion("big_slime_eat06");
		bigEatRegions[6] = this.bigAlien01Atlas.findRegion("big_slime_eat07");
		bigEatRegions[7] = this.bigAlien01Atlas.findRegion("big_slime_eat08");
		bigEatRegions[8] = this.bigAlien01Atlas.findRegion("big_slime_eat07");
		bigEatRegions[9] = this.bigAlien01Atlas.findRegion("big_slime_eat06");
		bigEatRegions[10] = this.bigAlien01Atlas.findRegion("big_slime_eat05");
		bigEatRegions[11] = this.bigAlien01Atlas.findRegion("big_slime_eat04");
		bigEatRegions[12] = this.bigAlien01Atlas.findRegion("big_slime_eat03");
		this.bigAlienEat01 = new Animation(1/6f, bigEatRegions);
		
		TextureRegion[] bigMoveRegions = new TextureRegion[4];
		bigMoveRegions[0] = this.bigAlien01Atlas.findRegion("big_slime_moving01");
		bigMoveRegions[1] = this.bigAlien01Atlas.findRegion("big_slime_moving02");
		bigMoveRegions[2] = this.bigAlien01Atlas.findRegion("big_slime_moving03");
		bigMoveRegions[3] = this.bigAlien01Atlas.findRegion("big_slime_moving02");
		this.bigAlienMove01 = new Animation(1/6f, bigMoveRegions);
		
		this.station01Atlas = new TextureAtlas(Gdx.files.internal("station01.atlas"));
		TextureRegion[] stationRegions = new TextureRegion[13];
		stationRegions[0] = this.station01Atlas.findRegion("station01");
		stationRegions[1] = this.station01Atlas.findRegion("station02");
		stationRegions[2] = this.station01Atlas.findRegion("station03");
		stationRegions[3] = this.station01Atlas.findRegion("station04");
		stationRegions[4] = this.station01Atlas.findRegion("station05");
		stationRegions[5] = this.station01Atlas.findRegion("station06");
		stationRegions[6] = this.station01Atlas.findRegion("station07");
		stationRegions[7] = this.station01Atlas.findRegion("station08");
		stationRegions[8] = this.station01Atlas.findRegion("station07");
		stationRegions[9] = this.station01Atlas.findRegion("station06");
		stationRegions[10] = this.station01Atlas.findRegion("station05");
		stationRegions[11] = this.station01Atlas.findRegion("station04");
		stationRegions[12] = this.station01Atlas.findRegion("station03");
		this.station01Animation = new Animation(1/6f, stationRegions);
		
		this.eye01Atlas = new TextureAtlas(Gdx.files.internal("eye01.atlas"));
		this.eye01Animation = new Animation(1f, eye01Atlas.getRegions());
		
    	this.angry01Atlas = new TextureAtlas(Gdx.files.internal("angry01.atlas"));
        this.angry01Animation = new Animation(1/8f, angry01Atlas.getRegions());
   
    	this.hello01Atlas = new TextureAtlas(Gdx.files.internal("hello01.atlas"));
        this.hello01Animation = new Animation(1/8f, hello01Atlas.getRegions());
    	
        this.idle01Atlas = new TextureAtlas(Gdx.files.internal("idle01.atlas"));
        this.idle01Animation = new Animation(1/8f, idle01Atlas.getRegions());
        
        this.idle02Atlas = new TextureAtlas(Gdx.files.internal("idle02.atlas"));
        this.idle02Animation = new Animation(1/8f, idle02Atlas.getRegions());
        
        this.screenBackgroundAtlas = new TextureAtlas(Gdx.files.internal("screen01.atlas"));
        this.screenBackgroundAnimation = new Animation(1/8f, screenBackgroundAtlas.getRegions());
    }
    
    private Map<String,Map<Integer,BitmapFont>> loadFonts(boolean shadow) {
    	String shadowString = "";
    	if (shadow) {
    		shadowString = "_shadow";
    	}
    	
    	Map<String,Map<Integer,BitmapFont>> fonts = new HashMap<String,Map<Integer,BitmapFont>>();
    	for (String name : this.getFontNames()) {
    		Map<Integer,BitmapFont> oneFont = new HashMap<Integer,BitmapFont>();
    		int startSize = 20;
    		int endSize = 60;
    		int increment = 5;
    		for (int i = startSize; i <= endSize; i+=increment) {
    	    	FileHandle infoHandle = Gdx.files.internal("fonts/" + name + shadowString + "_" + String.valueOf(i) + ".fnt");
    	    	FileHandle textureHandle = Gdx.files.internal("fonts/" + name + shadowString + "_" + String.valueOf(i) + ".png");
    	    	BitmapFont theFont = new BitmapFont(infoHandle, textureHandle, false);
    			oneFont.put(i, theFont);
    		}
    		fonts.put(name, oneFont);
    	}
    	return fonts;
    }
    private List<String> getFontNames() {
    	List<String> fontNames = new ArrayList<String>();
    	fontNames.add("otherf");
    	return fontNames;
    }
    private void configureFonts() {
    	//FileHandle infoHandle = Gdx.files.internal("fonts/" + "otherf_shadow_125"+ ".fnt");
    	//FileHandle textureHandle = Gdx.files.internal("fonts/" + "otherf_shadow_125" + ".png");
    	//this.titleFont = new BitmapFont(infoHandle, textureHandle, false);
    	this.titleFont = this.fontsShadow.get("otherf").get(60);
    	
	    this.bigButtonFont = this.fontsShadow.get("otherf").get(40);
	    this.smallButtonFont = this.fontsShadow.get("otherf").get(30);
    }
    
    private Map<MusicType, Music> loadMusic() {
    	Map<MusicType, Music> music = new HashMap<MusicType, Music>();
    	for (MusicType type : MusicType.values()) {
    		music.put(type, Gdx.audio.newMusic(Gdx.files.internal(type.path)));
    	}
    	return music;
    }
    
    private Map<SoundType, Sound> loadSounds() {
    	Map<SoundType, Sound> sounds = new HashMap<SoundType, Sound>();
    	for (SoundType type : SoundType.values()) {
    		sounds.put(type, Gdx.audio.newSound(Gdx.files.internal(type.path)));
    	}
    	return sounds;
    }
    
	private void configureInputHandlers() {
		this.inputHandler.addProcessor(this.hudStage); 
        this.inputHandler.addProcessor(this.stage);
        
        this.cameraPanner = new CameraPanner(this.camera);
        this.cameraPanner.setEnabled(true);
        this.cameraPanner.setParallaxCamera(this.parallaxCamera);
        
        this.pinchZoomer = new PinchZoomer(this.camera);
        this.pinchZoomer.setEnabled(true);
        this.scrollWheelZoomer = new ScrollWheelZoomer(this.camera);
        this.scrollWheelZoomer.setEnabled(true);
        this.inputHandler.addProcessor(new GestureDetector(this.cameraPanner));
        this.inputHandler.addProcessor(new GestureDetector(this.pinchZoomer));
        this.inputHandler.addProcessor(this.scrollWheelZoomer);
        Gdx.input.setInputProcessor(this.inputHandler);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		super.render();
		
        parallaxCamera.update();
        parallaxBatch.setProjectionMatrix(parallaxCamera.calculateParallaxMatrix(0.05f, 0.05f));
        parallaxBatch.begin();
        parallaxDrawer.drawBottomLayer();
        parallaxBatch.end();
        parallaxBatch.setProjectionMatrix(parallaxCamera.calculateParallaxMatrix(0.4f, 0.4f));
        parallaxBatch.begin();
        parallaxDrawer.drawMiddleLayer();
        parallaxBatch.end();
        parallaxBatch.setProjectionMatrix(parallaxCamera.calculateParallaxMatrix(0.7f, 0.7f));
        parallaxBatch.begin();
        parallaxDrawer.drawTopLayer();
        parallaxBatch.end();
        
		backgroundCamera.update();
		backgroundStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        backgroundBatch.setProjectionMatrix(backgroundCamera.combined);
        backgroundBatch.begin();
        backgroundBatch.end();
        backgroundStage.draw();
        
        camera.update();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
		batch.end();
		
		fogStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		fogStage.draw();
		fogBatch.setProjectionMatrix(camera.combined);
		fogBatch.begin();
		fogBatch.end();
		
        hudCamera.update();
        hudBatch.setProjectionMatrix(hudCamera.combined);
        hudBatch.begin();
        //hudBatch.draw(bg, 0, 0, STAGE_WIDTH, STAGE_HEIGHT);
        hudBatch.end();
        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hudStage.draw();
	}
	
	private Map<StarType, TextureRegion> loadStarTextures() {
		Map<StarType, TextureRegion> textures =  new HashMap<StarType, TextureRegion>();
		this.starAtlasForDispose = new TextureAtlas("stars01.atlas");
		for (StarType type : StarType.values()) {
			AtlasRegion region = this.starAtlasForDispose.findRegion(type.fileName);
			TextureRegion textureRegion = region;
			textures.put(type, textureRegion);
		}
		return textures;
	}
	
	private Map<ShipType, TextureRegion> loadShipTextures() {
		Map<ShipType, TextureRegion> textures =  new HashMap<ShipType, TextureRegion>();
		this.shipAtlasForDispose = new TextureAtlas("ships01.atlas");
		for (ShipType type : ShipType.values()) {
			AtlasRegion region = this.shipAtlasForDispose.findRegion(type.fileName);
			TextureRegion textureRegion = region;
			textures.put(type, textureRegion);
		}
		return textures;
	}
	
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.backgroundStage.getViewport().update(width, height, false);
        this.stage.getViewport().update(width, height, false);
        this.fogStage.getViewport().update(width, height, false);
        this.hudStage.getViewport().update(width, height, true);
    }
	
	@Override
	public void dispose() {
		this.gridBackground.dispose();
		this.radarRing01.dispose();
		this.alien01.dispose();
		this.aiPlanetCover01.dispose();
		this.playerPlanetCover01.dispose();
		this.laser01.dispose();
		this.blackCircle.dispose();
		this.nebula02.dispose();
		this.nebula03.dispose();
		this.nebula04.dispose();
		this.nebula05.dispose();
		this.screenOverlay01.dispose();
		this.seamlessSpace.dispose();
		this.seamlessSpaceTrans.dispose();
		
		this.starAtlasForDispose.dispose();
		this.shipAtlasForDispose.dispose();
		
		this.angry01Atlas.dispose();
		this.hello01Atlas.dispose();
		this.idle01Atlas.dispose();
		this.idle02Atlas.dispose();
		this.screenBackgroundAtlas.dispose();
		this.bigAlien01Atlas.dispose();
		this.eye01Atlas.dispose();
		this.station01Atlas.dispose();
		
		this.titleFont.dispose();
		this.bigButtonFont.dispose();
		this.smallButtonFont.dispose();
		
		for (Music music : this.music.values()) {
			music.dispose();
		}
		for (Sound sound : this.sounds.values()) {
			sound.dispose();
		}
	}
}