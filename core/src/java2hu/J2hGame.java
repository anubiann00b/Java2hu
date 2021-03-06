
package java2hu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java2hu.events.Event;
import java2hu.events.EventHandler;
import java2hu.events.EventListener;
import java2hu.events.ICancellable;
import java2hu.events.input.KeyDownEvent;
import java2hu.events.sound.MusicModifierChangeEvent;
import java2hu.events.sound.SoundModifierChangeEvent;
import java2hu.gameflow.GameFlowScheme;
import java2hu.menu.PauseMenu;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.LaserDrawer;
import java2hu.object.enemy.Enemy;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.overwrite.J2hObject;
import java2hu.plugin.Plugin;
import java2hu.spellcard.Spellcard;
import java2hu.touhou.font.TouhouFont;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.HitboxUtil;
import java2hu.util.MathUtil;
import java2hu.util.Scheduler;
import java2hu.util.Setter;

import javafx.embed.swing.JFXPanel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.google.common.collect.Sets;

/**
 * The base game class, this contains everything you need to create a simple game.
 * Bullet/Enemy/Player/Entity spawning all happens here.
 * This will also run a GameFlowScheme
 * Spell cards can also be ran form here.
 * And the final drawing/updating is also done here.
 * Basically the nerve center of the whole application.
 * Be sure to extend this and make your changes, it will do nothing on it's own.
 */
public class J2hGame extends ApplicationAdapter implements InputProcessor
{
	public static final int GUI_Z_ORDER = 9999999;
	
	/**
	 * Aspect Ratio camera, holds an instance of OrthographicCamera, but forces it to obey selected aspect ratio.
	 */
	public AspectRatioCamera camera;
	
	/**
	 * Standard SpriteBatch used to draw pretty much everything.
	 */
	public SpriteBatch batch;
	
	/**
	 * Standard font, defaults to the default touhou font. (Russel Square)
	 */
	public BitmapFont font;
	
	/**
	 * Standard shaperenderer, used for rendering hitboxes.
	 */
	public ShapeRenderer shape;
	
	/**
	 * Standard Model Batch, used for rendering models
	 */
	public ModelBatch modelBatch;
	
	/**
	 * Assets Manager, if you want to load stuff async.
	 */
	public AssetManager assets;
	
	/**
	 * Extendable methods to change how the game works.
	 */
	
	/**
	 * Ran before the game starts running the @Loader, you can spawn a loading screen here.
	 */
	public void onLoadStart()
	{
		
	}
	
	/**
	 * Ran once the @Loader is done loading everything.
	 */
	public void onLoadFinished()
	{
		
	}
	
	private PauseMenu pauseMenu;
	
	/**
	 * Ran once the player pauses the game.
	 * NOT ran if you use code to set the pause state.
	 */
	public void onPause()
	{
		setPaused(true);
		
		TouhouSounds.Hud.PAUSE.play();
		pauseMenu = new PauseMenu(null);
		spawn(pauseMenu);
	}
	
	/**
	 * Ran once the player un-pauses the game.
	 * NOT ran if you use code to set the pause state.
	 */
	public void onDePause()
	{
		setPaused(false);
		
		if(pauseMenu == null)
			return;
		
		delete(pauseMenu);
		pauseMenu.disposeAll();
		pauseMenu = null;
	}
	
	/**
	 * Ran once the player uses the retry button in the standard pause menu.
	 */
	public void onRetry()
	{
		
	}
	
	/**
	 * Ran once the player uses the to title button in the standard pause menu.
	 */
	public void onToTitle()
	{
		
	}
	
	/**
	 * Ran once a game starts. (Should be manually called)
	 */
	public void onStartGame()
	{
		
	}
	
	private GameFlowScheme scheme;
	
	/**
	 * See @GameFlowScheme
	 * @return
	 */
	public GameFlowScheme getScheme()
	{
		return scheme;
	}
	
	public void setScheme(GameFlowScheme scheme)
	{
		this.scheme = scheme;
	}
	
	private Player player;
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void spawn(Player player)
	{
		this.player = player;
		
		stageObjects.add(player);
		player.onSpawn();
		player.update(getActiveTick());
	}
	
	public void clearPlayer()
	{
		if(player != null)
		{
			delete(player);
		}
		
		player = null;
	}
	
	private RenderSet<Spellcard> spellcards = new RenderSet<Spellcard>();
	
	public void startSpellCard(Spellcard card)
	{
		spellcards.add(card);
	}
	
	public void stopSpellCard(Spellcard card)
	{
		spellcards.remove(card);
	}
	
	public void stopAllSpellCardsFrom(StageObject owner)
	{
		Iterator<Spellcard> it = spellcards.iterator();
		
		while(it.hasNext())
		{
			Spellcard card = it.next();
			
			if(card.getOwner() == owner)
				it.remove();
		}
	}
	
	public void clearSpellcards()
	{
		for(Spellcard card : getSpellcards())
		{
			card.onRemove();
		}
		
		spellcards.clear();
	}
	
	public Set<Spellcard> getSpellcards()
	{
		return spellcards;
	}
	
	private RenderSet<StageObject> stageObjects = new RenderSet<StageObject>();
	
	public void spawn(StageObject object)
	{
		if(object instanceof Bullet)
		{
			spawn((Bullet)object);
			return;
		}
		
		if(object instanceof Player)
		{
			spawn((Player)object);
			return;
		}
		
		stageObjects.add(object);
		object.onSpawn();
		object.update(getActiveTick());
	}
	
	public void delete(StageObject object)
	{
		stageObjects.remove(object);
		bullets.remove(object);
		
		object.onDelete();
	}
	
	public Set<StageObject> getStageObjects()
	{
		return stageObjects;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Set<T> getStageObjects(Class<T> clazz)
	{
		HashSet<T> set = new HashSet<T>();
		
		for(StageObject obj : getStageObjects())
		{
			if(obj.getClass().isAssignableFrom(clazz))
			{
				set.add((T)obj);
			}
		}
		
		return set;
	}
	
	private RenderSet<Bullet> bullets = new RenderSet<Bullet>();
	
	public void spawn(Bullet object)
	{
		bullets.add(object);
		object.onSpawn();
		object.update(getActiveTick());
	}
	
	public void delete(Bullet object)
	{
		bullets.remove(object);
		object.onDelete();
	}
	
	public static enum ClearType
	{
		ALL,
		ALL_OBJECTS,
		LIVING,
		NON_BOSS_LIVING, 
		BULLETS,
		PLUGINS,
		/**
		 * Spells don't get cleared by @ClearType.ALL
		 */
		SPELLS,
		/**
		 * Game tasks only.
		 * Also doesn't get cleared by @ClearType.ALL
		 */
		TASKS;
		
		boolean ignorePersistant = false;
	}
	
	public static class Clear
	{
		private ClearType type;
		private Boolean ignorePersistant;
		
		public Clear(ClearType type)
		{
			this(type, false);
		}
		
		public Clear(ClearType type, boolean ignorePersistant)
		{
			this.type = type;
			this.ignorePersistant = ignorePersistant;
		}
		
		public ClearType getType()
		{
			return type;
		}
		
		public Boolean doIgnorePersistant()
		{
			return ignorePersistant;
		}
	}
	
	public void clear(ClearType... types)
	{
		for(ClearType type : types)
		{
			clear(type, false);
		}
	}
	
	public void clear(ClearType type, boolean ignorePersistance)
	{
		System.out.println("Clearing with type: " + type + " ignore:" + ignorePersistance);
		
		clear(new Clear(type, ignorePersistance));
	}
	
	public void clear(Clear... clearDatas)
	{
		final HashMap<ClearType, Boolean> types = new HashMap<J2hGame.ClearType, Boolean>();
		
		Clear clearSpells = null;
		Clear clearTasks = null;
		Clear clearAll = null;
		Clear clearAllObjects = null;
		
		for(Clear type : clearDatas)
		{
			if(type.getType() == ClearType.SPELLS)
			{
				clearSpells = type;
				continue;
			}
			
			if(type.getType() == ClearType.TASKS)
			{
				clearTasks = type;
				continue;
			}

			if(type.getType() == ClearType.ALL)
			{
				clearAll = type;
				System.out.println("Clear all!");
				continue;
			}
			
			if(type.getType() == ClearType.ALL_OBJECTS)
			{
				clearAllObjects = type;
			}
			
			types.put(type.getType(), type.doIgnorePersistant());
		}
		
		if(clearSpells != null || clearAll != null)
		{
			clearSpellcards();
		}
		
		if(clearTasks != null || clearAll != null)
		{
			delayedGameTasks.clear();
		}
		
		final Clear finalClearAll = clearAll;
		
		final Clear finalClearAllObjects = clearAllObjects;
		
		Runnable clear = new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<StageObject> toDelete = new ArrayList<StageObject>();
				
				for (StageObject obj : Game.getGame().getStageObjects())
				{
					boolean persistant = obj.isPersistant();
					
					if (finalClearAll != null || finalClearAllObjects != null)
					{
						if (!persistant || finalClearAll != null && finalClearAll.ignorePersistant || finalClearAllObjects != null && finalClearAllObjects.ignorePersistant)
						{
							toDelete.add(obj);
							continue;
						}
					}
					
					boolean isLiving = obj instanceof LivingObject;
					boolean isBoss = obj instanceof Boss;
					
					if (isLiving)
					{
						if (types.containsKey(ClearType.LIVING))
						{
							if (!persistant || types.get(ClearType.LIVING))
							{
								toDelete.add(obj);
								continue;
							}
						}
						
						if (!isBoss && types.containsKey(ClearType.NON_BOSS_LIVING))
						{
							if (!persistant || types.get(ClearType.NON_BOSS_LIVING))
							{
								toDelete.add(obj);
								continue;
							}
						}
					}
					
					boolean removePlugins = types.containsKey(ClearType.PLUGINS);
					
					if (removePlugins)
					{
						Iterator<Plugin> it = obj.getEffects().iterator();
						
						while (it.hasNext())
						{
							Plugin plugin = it.next();
							
							if (!plugin.isPersistant() || types.get(ClearType.PLUGINS))
							{
								plugin.onDelete();
								it.remove();
							}
						}
						continue;
					}
				}
				
				for (StageObject obj : toDelete)
				{
					delete(obj);
				}
				
				toDelete.clear();
				
				for (Bullet bullet : getBullets())
				{
					boolean clearBullets = types.containsKey(ClearType.BULLETS);
					
					if (clearBullets || finalClearAll != null || types.containsKey(ClearType.ALL_OBJECTS))
					{
						if (!bullet.isPersistant() || clearBullets && types.get(ClearType.BULLETS).booleanValue() || finalClearAll != null && finalClearAll.ignorePersistant || finalClearAllObjects != null && finalClearAllObjects.ignorePersistant)
						{
							toDelete.add(bullet);
							continue;
						}
					}
				}
				
				for (StageObject obj : toDelete)
				{
					obj.onDelete();
					getBullets().remove(obj);
				}
				
				toDelete.clear();
			}
		};
		
		clear.run();
	}
	
	/**
	 * Clears all objects
	 */
	public void clearObjects()
	{
		clear(ClearType.ALL_OBJECTS);
	}
	
	public Set<Bullet> getBullets()
	{
		return bullets;
	}
	
	private RenderSet<Task> delayedGameTasks = new RenderSet<Task>();
	private RenderSet<Task> delayedPauseTasks = new RenderSet<Task>();
	private RenderSet<Task> delayedTasks = new RenderSet<Task>();
	private RenderSet<Runnable> runAfterRender = new RenderSet<Runnable>();
	
	private class Task
	{
		private Runnable runnable;
		private long tick;
	}
	
	private long tick = 0; // Game ticker
	private long pauseTick = 0; // Pause ticker
	
	private float elapsedTime = 0; // Elapsed game time
	private float pauseElapsedTime = 0; // Elapsed pause time
	
	private long internalTick = 0; // Pause ticker
	private float internalElapsedTime = 0; // Elapsed pause time
	
	private int width = 1280;
	private int height = 960;
	
	private Setter<Float> transformation;
	
	public void setTransformation(Setter<Float> transformation)
	{
		this.transformation = transformation;
	}
	
	public Setter<Float> getTransformation()
	{
		return transformation;
	}
	
	private Set<Runnable> musicThreadQueue = Sets.newSetFromMap(new ConcurrentHashMap<Runnable, Boolean>());
	
	private Thread musicThread = new Thread()
	{
		@Override
		public synchronized void start()
		{
			super.start();
			this.setName("Music Thread (JavaFX)");
		};
		
		@Override
		public void run()
		{
			new JFXPanel(); // Initialize JavaFX environment so we can use JavaFX's MediaPlayer, which _can_ set starting positions.
			
			while(true)
			{
				Iterator<Runnable> it = musicThreadQueue.iterator();

				while(it.hasNext())
				{
					Runnable run = it.next();

					try
					{
						run.run();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					System.out.println("[Music Thread] Executed something on the music thread succesfully.");

					it.remove();
				}

				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};
	};
	
	public void runOnMusicThread(Runnable run)
	{
		musicThreadQueue.add(run);
	}
	
	private float musicModifier = 1f;
	
	/**
	 * Music modifier, applied to all @J2hMusic objects automatically.
	 * @return
	 */
	public float getMusicModifier()
	{
		return musicModifier;
	}
	
	public void setMusicModifier(float musicModifier)
	{
		MusicModifierChangeEvent event = new MusicModifierChangeEvent(this.musicModifier, musicModifier);
		
		callEvent(event);
		
		if(event.isCancelled())
			return;
		
		this.musicModifier = event.getNewValue();
	}
	
	private float soundModifier = 1f;
	
	/**
	 * Sound modifier, applied to all @J2hSound objects automatically.
	 * @return
	 */
	public float getSoundModifier()
	{
		return soundModifier;
	}
	
	public void setSoundModifier(float soundModifier)
	{
		SoundModifierChangeEvent event = new SoundModifierChangeEvent(this.soundModifier, soundModifier);
		
		callEvent(event);
		
		if(event.isCancelled())
			return;
		
		this.soundModifier = event.getNewValue();
	}
	
	/**
	 * Run a task delayed while the game ticker is enabled.
	 * @param run
	 * @param ticks
	 */
	public void addTaskGame(Runnable run, int ticks)
	{
		long tick = getTick() + ticks;
		
		Task task = new Task();
		task.tick = tick;
		task.runnable = run;
		
		delayedGameTasks.add(task);
	}
	
	/**
	 * Run a task delayed while the pause ticker is enabled.
	 * @param run
	 * @param ticks
	 */
	public void addTaskPause(Runnable run, int ticks)
	{
		long tick = getPauseTick() + ticks;
		
		Task task = new Task();
		task.tick = tick;
		task.runnable = run;
		
		delayedPauseTasks.add(task);
	}
	
	/**
	 * Run a task delayed
	 * @param run
	 * @param ticks
	 */
	public void addTask(Runnable run, int ticks)
	{
		long tick = internalTick + ticks;
		
		Task task = new Task();
		task.tick = tick;
		task.runnable = run;
		
		delayedTasks.add(task);
	}
	
	private void addAfterRenderTask(Runnable run)
	{
		runAfterRender.add(run);
	}
	
	/**
	 * Get the minimum X value of the game's boundary, extend this with your own values to specify where the game is taking place. (To make overlays)
	 */
	public float getMinX()
	{
		return 0;
	}
	
	/**
	 * Get the center value for the Y axis, this is the point in the middle between {@link #getMinY()} and {@link #getMaxY()}.
	 * Do NOT extend this for your game, it is calculated automatically from the 2 methods above.
	 */
	public float getCenterX()
	{
		return (float) (getMinX() + ((getMaxX() - getMinX()) / 2d));
	}
	
	/**
	 * Get the maximum X value of the game's boundary, extend this with your own values to specify where the game is taking place. (To make overlays)
	 */
	public float getMaxX()
	{
		return Game.getGame().getWidth();
	}
	
	/**
	 * Get the minimum Y value of the game's boundary, extend this with your own values to specify where the game is taking place. (To make overlays)
	 */
	public float getMinY()
	{
		return 0;
	}
	
	/**
	 * Get the center value for the Y axis, this is the point in the middle between {@link #getMinY()} and {@link #getMaxY()}.
	 * Do NOT extend this for your game, it is calculated automatically from the 2 methods above.
	 */
	public float getCenterY()
	{
		return (float) (getMinY() + ((getMaxY() - getMinY()) / 2d));
	}
	
	/**
	 * Get the maximum Y value of the game's boundary, extend this with your own values to specify where the game is taking place. (To make overlays)
	 */
	public float getMaxY()
	{
		return Game.getGame().getHeight();
	}
	
	/**
	 * Get the width of the screen, this is NOT the boundary's width if the boundary is not 1:1.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Get the height of the screen, his is NOT the boundary's height if the boundary is not 1:1.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Returns the values from {@link #getMinX()}, {@link #getMaxX()}, {@link #getMinY()}, {@link #getMaxY()} to make a rectangle.
	 */
	public Rectangle getBoundary()
	{
		return new Rectangle((int)getMinX(), (int)getMinY(), (int)(getMaxX() - getMinX()), (int)(getMaxY() - getMinY()));
	}
	
	/**
	 * Returns true if the specific position is inside the game boundary, useful for deletion if out of the boundary, for instance.
	 * By default, all bullets will be deleted if they stray {@link Bullet#getDeleteDistance()} distance from the boundary.
	 */
	public boolean inBoundary(float x, float y)
	{
		return x > getMinX() && x < getMaxX() && y > getMinY() && y < getMaxY();
	}
	
	public static enum TickType
	{
		GAME, PAUSE;
	}
	
	public long getTick()
	{
		return tick;
	}
	
	public long getPauseTick()
	{
		return pauseTick;
	}
	
	/**
	 * While paused: Returns pause ticks
	 * While not paused: Returns game ticks
	 * @return
	 */
	public long getActiveTick()
	{
		if(isPaused())
			return getPauseTick();
		
		return getTick();
	}
	
	public TickType getActiveTickType()
	{
		if(isPaused())
			return TickType.PAUSE;
		
		return TickType.GAME;
	}
	
	public float getElapsedTime()
	{
		return elapsedTime;
	}
	
	public float getPauseElapsedTime()
	{
		return pauseElapsedTime;
	}
	
	/**
	 * While paused: Returns elapsed pause time
	 * While not paused: Returns elapsed game time
	 * @return
	 */
	public float getActiveElapsedTime()
	{
		if(isPaused())
			return getPauseElapsedTime();
		
		return getElapsedTime();
	}
	
	private boolean debugMode = false;
	
	public boolean isDebugMode()
	{
		return debugMode;
	}
	
	public void setDebugMode(boolean debugMode)
	{
		this.debugMode = debugMode;
		
		System.out.println("Debug Mode: " + (debugMode ? "ENABLED" : "DISABLED"));
	}

	private boolean paused = false;
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean paused)
	{
		this.paused = paused;
		
		if(!paused)
		{
			pauseTick = 0;
			pauseElapsedTime = 0;
		}
		
		System.out.println(paused ? "Game Paused" : "Game (Re)started");
	}
	
	private boolean outOfGame = true;
	
	public boolean isOutOfGame()
	{
		return outOfGame;
	}
	
	public void setOutOfGame(boolean outOfGame)
	{
		this.outOfGame = outOfGame;
	}
	
	private boolean profiling = false;
	
	public boolean isProfiling()
	{
		return profiling;
	}
	
	public void setProfiling(boolean profiling)
	{
		this.profiling = profiling;
		System.out.println("Profiling " + (profiling ? "ENABLED" : "DISABLED"));
	}
	
	private boolean created = false;

	@Override
	public void create()
	{
		Game.singleton = this;
		J2hObject.game = this;
		
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();
		
		font = TouhouFont.get(16);
		shape = new ShapeRenderer(2000000);
		assets = new AssetManager();
		musicThread.start();
		
		camera = new AspectRatioCamera(width, height);
		camera.camera.position.set(width/2, height/2, 0);
		
		onLoadStart();
		
		Gdx.input.setInputProcessor(this);
		
		created = true;
		
		StartLoader.loadStartup(new Runnable() { @Override
		public void run() { onLoadFinished(); } });
	}
	
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		if(!created)
			return;
		
		camera = new AspectRatioCamera(width, height);
		camera.camera.position.set(width/2, height/2, 0);
	}
	
	public Matrix4 standardProjectionMatrix = null;

	private ArrayList<String> profilingOutput;
	
	/**
	 * Draw items such as hitboxes.
	 */
	public void drawDebugData()
	{
		shape.begin(ShapeType.Line);

		for(Bullet bullet : bullets)
		{
			Polygon current = bullet.getHitbox();

			if(bullet instanceof LaserDrawer)
			{
				for(Position p : ((LaserDrawer)bullet).getPoints())
				{
					if(Float.isNaN(p.getX()))
						continue;

					shape.circle(p.getX(), p.getY(), 1.5f);
				}
			}


			{
				if(current != null)
					HitboxUtil.drawHitbox(current);
			}
		}

		for(StageObject object : stageObjects)
		{
			if(object instanceof LivingObject)
				HitboxUtil.drawHitbox(((LivingObject)object).getHitbox());

			if(object instanceof Boss)
			{
				HitboxUtil.drawHitbox(((Boss)object).getPlayerHitHitbox());
			}

			if(object instanceof Enemy)
			{
				HitboxUtil.drawHitbox(((Enemy)object).getPlayerHitHitbox());
			}
		}

		if(player != null)
			HitboxUtil.drawHitbox(player.getHitbox());

		shape.end();
	}
	
	
	/**
	 * This method runs through all stage objects and updates them.
	 * You should generally NOT extends this method unless you know what you're doing.
	 */
	public void updateStageDelta()
	{
		final HashMap<Object, Long> updateTimes;

		if(profiling)
		{
			updateTimes = new HashMap<Object, Long>();
		}
		else
		{
			updateTimes = null;
		}

		{
			Iterator<StageObject> it = stageObjects.renderIterator();

			stageObjects.startReading();

			while(it.hasNext())
			{
				StageObject object = it.next();

				long startTime = 0;

				if(profiling)
					startTime = System.nanoTime();

				if(!isPaused() || object.isActiveDuringPause())
					object.update(Gdx.graphics.getDeltaTime());

				if(profiling)
					updateTimes.put(object, System.nanoTime() - startTime);
			}
			
			stageObjects.endReading();
		}

		{
			Iterator<Bullet> it = bullets.renderIterator();

			bullets.startReading();

			while(it.hasNext())
			{
				Bullet bullet = it.next();

				long startTime = 0;

				if(profiling)
					startTime = System.nanoTime();

				if(!isPaused() || bullet.isActiveDuringPause())
					bullet.update(Gdx.graphics.getDeltaTime());

				if(profiling)
					updateTimes.put(bullet, System.nanoTime() - startTime);
			}
			
			bullets.endReading();
		}

		if(profiling)
		{
			ArrayList<Object> sortedList = new ArrayList<Object>(updateTimes.keySet());

			Collections.sort(sortedList, new Comparator<Object>()
					{
				@Override
				public int compare(Object o1, Object o2)
				{
					if(o1 == null && o2 == null)
						return 0;

					if(o1 == null)
						return -1;

					if(o2 == null)
						return 1;

					return (int) (updateTimes.get(o2) - updateTimes.get(o1));
				}
					});

			profilingOutput.add("Update (delta loop) lag sources:");

			for(int i = 0; i < 10 && i < sortedList.size(); i++)
			{
				Object obj = sortedList.get(i);
				float ms = (float)updateTimes.get(obj) / 1000000;

				if(ms > 2)
					profilingOutput.add(obj + " - " + ms + "ms.");
			}
		}
	}

	/**
	 * This method runs through all stage objects and updates them.
	 * You should generally NOT extends this method unless you know what you're doing.
	 */
	public void updateStageLogic()
	{
		final HashMap<Object, Long> updateTimes;

		if(profiling)
		{
			updateTimes = new HashMap<Object, Long>();
		}
		else
		{
			updateTimes = null;
		}

		{
			Iterator<StageObject> it = stageObjects.renderIterator();
			
			stageObjects.startReading();

			while(it.hasNext())
			{
				StageObject object = it.next();

				long startTime = 0;

				if(profiling)
					startTime = System.nanoTime();

				if(!isPaused() || object.isActiveDuringPause())
					object.update(tick);

				if(profiling)
					updateTimes.put(object, System.nanoTime() - startTime);
			}
			
			stageObjects.endReading();
		}

		if(!isPaused())
		{
			Iterator<Spellcard> it = spellcards.renderIterator();
			
			spellcards.startReading();
			
			while(it.hasNext())
			{
				Spellcard card = it.next();
				
				long startTime = 0;

				if(profiling)
					startTime = System.nanoTime();

				card.run();

				if(profiling)
					updateTimes.put(card, System.nanoTime() - startTime);
			}
			
			spellcards.endReading();
		}

		{
			Iterator<Bullet> it = bullets.renderIterator();
			
			bullets.startReading();

			while(it.hasNext())
			{
				Bullet bullet = it.next();

				long startTime = 0;

				if(profiling)
					startTime = System.nanoTime();

				if(!isPaused() || bullet.isActiveDuringPause())
					bullet.update(tick);

				if(profiling)
					updateTimes.put(bullet, System.nanoTime() - startTime);
			}
			
			bullets.endReading();
		}
//
//		{
//			long startTime = 0;
//
//			if(profiling)
//				startTime = System.nanoTime();
//
//			if(player != null && (!isPaused() || player.isActiveDuringPause()))
//				player.update(tick);
//
//			if(profiling)
//				updateTimes.put(player, System.nanoTime() - startTime);
//		}

		if(profiling)
		{
			ArrayList<Object> sortedList = new ArrayList<Object>(updateTimes.keySet());

			Collections.sort(sortedList, new Comparator<Object>()
					{
				@Override
				public int compare(Object o1, Object o2)
				{
					if(o1 == null && o2 == null)
						return 0;

					if(o1 == null)
						return -1;

					if(o2 == null)
						return 1;

					return (int) (updateTimes.get(o2) - updateTimes.get(o1));
				}
					});

			profilingOutput.add("Update (logic loop) lag sources:");

			for(int i = 0; i < 10 && i < sortedList.size(); i++)
			{
				Object obj = sortedList.get(i);
				float ms = (float)updateTimes.get(obj) / 1000000;

				if(ms > 2)
					profilingOutput.add(obj + " - " + ms + "ms.");
			}
		}

		if(!isPaused())
		{
			Iterator<Task> it = delayedGameTasks.renderIterator();
			
			delayedGameTasks.startReading();
			
			while(it.hasNext())
			{
				Task t = it.next();
				
				if(t != null && t.tick <= getTick())
				{
					t.runnable.run();
					it.remove();
				}
			}
			
			delayedGameTasks.endReading();
		}
		else
		{
			Iterator<Task> it = delayedPauseTasks.renderIterator();
			
			delayedPauseTasks.startReading();
			
			while(it.hasNext())
			{
				Task t = it.next();
				
				if(t != null && t.tick <= getPauseTick())
				{
					t.runnable.run();
					it.remove();
				}
			}
			
			delayedPauseTasks.endReading();
		}
		
		Iterator<Task> it = delayedTasks.renderIterator();
		
		while(it.hasNext())
		{
			delayedTasks.startReading();
			
			Task t = it.next();
			
			if(t != null && t.tick <= internalTick)
			{
				t.runnable.run();
				it.remove();
			}
			
			delayedTasks.endReading();
		}
//		
//		Iterator<Runnable> afterIt = runAfterRender.iterator();
//		
//		while(afterIt.hasNext())
//		{
//			Runnable t = afterIt.next();
//			
//			if(t != null)
//			{
//				t.run();
//				it.remove();
//			}
//		}
	}
	
	/**
	 * This method runs through all stage objects and draws them to the stage.
	 * You should generally NOT extends this method unless you know what you're doing.
	 */
	public void drawStage()
	{
		if(transformation != null)
			transformation.set(Gdx.graphics.getDeltaTime());

		ArrayList<StageObject> objects = new ArrayList<StageObject>();
		objects.addAll(stageObjects);
		objects.addAll(bullets);
//		objects.add(player);

		Collections.sort(objects, new Comparator<StageObject>()
				{
			@Override
			public int compare(StageObject o1, StageObject o2)
			{
				if(o1 == null && o2 == null)
					return 0;

				if(o1 == null)
					return -1;

				if(o2 == null)
					return 1;

				return o1.getZIndex() - o2.getZIndex();
			}
				});

		final HashMap<Object, Long> drawTimes;

		if(profiling)
		{
			drawTimes = new HashMap<Object, Long>();
		}
		else
		{
			drawTimes = null;
		}

		batch.begin();

		ShaderProgram shader = null;
		FrameBuffer buffer = null;
		
		FrameBuffer.unbind();
		
		for(StageObject object : objects)
		{
			if(object != null)
			{
				long startTime = 0;

				if(profiling)
					startTime = System.nanoTime();
				
				boolean swapBuffer = object.getFrameBuffer() != buffer;
				
				// Switch out buffer with next one.
				if(swapBuffer)
				{
					batch.flush();
					
					if(buffer != null)
						buffer.end();
					
					buffer = object.getFrameBuffer();
					
					if(buffer != null)
						buffer.begin();
				}

				boolean swapShader = object.getShader() != shader;

				// Switch out shader with next one.
				if(swapShader)
				{
					ShaderProgram newShader = object.getShader();
					batch.setShader(newShader);
					shader = newShader;
				}
				
				object.draw();

				if(profiling)
					drawTimes.put(object, System.nanoTime() - startTime);
			}
		}
		
		batch.flush();
		
		if(profiling)
		{
			ArrayList<Object> sortedList = new ArrayList<Object>(drawTimes.keySet());

			profilingOutput.add("Draw lag sources:");

			class Info
			{
				public String name;
				public int amount;
				public double timeTaken;
			}

			HashMap<Class, Info> map = new HashMap<>();

			for(int i = 0; i < sortedList.size(); i++)
			{
				Object obj = sortedList.get(i);
				double ms = (float)drawTimes.get(obj) / 1000000d;

				if(!map.containsKey(obj.getClass()))
				{
					map.put(obj.getClass(), new Info());
				}

				Info info = map.get(obj.getClass());

				if(info.name == null)
					info.name = obj.toString();

				info.amount++;
				info.timeTaken += ms;
			}

			ArrayList<Info> infos = new ArrayList<Info>(map.values());

			Collections.sort(infos, new Comparator<Info>()
					{
				@Override
				public int compare(Info o1, Info o2)
				{
					if(o1 == null && o2 == null)
						return 0;

					if(o1 == null)
						return -1;

					if(o2 == null)
						return 1;

					return (int) (o2.timeTaken - o1.timeTaken);
				}
					});

			for(int i = 0; i < 10 && i < infos.size(); i++)
			{
				Info info = infos.get(i);

				if(info.timeTaken > 2)
				{
					profilingOutput.add(info.name + " - " + info.amount + "x - " + info.timeTaken);
				}
			}
		}

		if(shader != null)
			batch.setShader(null);
	}
	
	/**
	 * For drawing UI components, these use a standard @Matrix4 without transformation.
	 */
	public void drawUI()
	{
		drawDebugUI();
	}
	
	public void drawDebugUI()
	{
		String fps = "FPS: " + Gdx.graphics.getFramesPerSecond();
		TextBounds bounds = font.getBounds(fps);
		font.draw(batch, fps, Game.getGame().getWidth() - bounds.width, Game.getGame().getHeight()); // Draws downwards.

		String bc = "BC: " + bullets.size();
		bounds = font.getBounds(bc);
		font.draw(batch, bc, Game.getGame().getWidth() - bounds.width, Game.getGame().getHeight() - bounds.height);
	}
	
	/**
	 * This library runs 2 loops.
	 * The first is the render() method, a loop at (FPS) tps, this loop connects to StageObjects in .onUpdate(float delta)
	 * This means, you can make movement at pixels per second, which will look really cool on higher frequency monitors,
	 * object velocities and position movement should always be done through this (And default classes do).
	 * 
	 * The second is a loop within the render() method (so on the GL thread)
	 * This loop is called at the {@value #LOGIC_TPS} ticks per second, and will only update if the delta time has passed 1/{@value #LOGIC_TPS}
	 * This loop is used for logic, mostly stuff you need to know your times for, when you for instance want to make a wave
	 * of bullets spawn over 1 second (With a {@link #LOGIC_TPS} of 60), you'll want to make it spawn 60 times, if you use delta time for this, you can't.
	 * Because delta time can't ensure it won't miss one of your logic gates (or it could call it twice!), because if the user's FPS is 2x the {@value #LOGIC_TPS}
	 * It's guaranteed to call most of your logic TWICE, unless it's extremely strict, which pushes up the chance it might miss it completely!
	 * So for this we have this loop, which only updates at certain intervals, which you count by as ticks, and they make a great way to make timed intervals.
	 */
	
	/**
	 * Ticks per second in the logic loop, higher loops require higher FPS from the user!
	 */
	public static final int LOGIC_TPS = 60;
	
	private float nextTick = 0; 
	private float deltaSkip = 0;

	@Override
	public void render()
	{
		if(profiling)
		{
			profilingOutput = new ArrayList<String>();
		}
		else
		{
			profilingOutput = null;
		}

		long start = 0;

		if(profiling)
		{
			start = System.nanoTime();
		}

		camera.applyAspectRatio();

		batch.setProjectionMatrix(camera.camera.combined);
		shape.setProjectionMatrix(camera.camera.combined);

		if(!isPaused())
		{
			elapsedTime += Gdx.graphics.getDeltaTime();
		}
		else
		{
			pauseElapsedTime += Gdx.graphics.getDeltaTime();
		}

		internalElapsedTime += Gdx.graphics.getDeltaTime();

		{
			if(standardProjectionMatrix == null)
			{
				standardProjectionMatrix = new Matrix4().set(camera.camera.combined);
			}

			drawStage();
			
			batch.setProjectionMatrix(standardProjectionMatrix);
			
			batch.flush();
			
			drawUI();
			
			batch.end();
			
			if(debugMode)
			{
				drawDebugData();
			}
			
			float secondsPerTick = 1f/LOGIC_TPS;
			boolean updateLogic = false;
			
			if(internalElapsedTime > nextTick)
			{
				updateLogic = true;
			}
			else
			{
				double diff = MathUtil.getDifference(internalElapsedTime, nextTick);
				double diffNext = MathUtil.getDifference(nextTick, internalElapsedTime + Gdx.graphics.getDeltaTime());
				
				if(diff < diffNext)
				{
					updateLogic = true;
				}
			}
			
			if(updateLogic)
			{
				nextTick = nextTick + secondsPerTick;
				
				while(nextTick <= internalElapsedTime)
				{
					
//					System.out.println("Frame skip!");
					nextTick += secondsPerTick; // Skip frame
					deltaSkip += secondsPerTick;
				}
				
				if(deltaSkip <= 0)
				{
					if(!isPaused())
					{
						tick++;
					}
					else
					{
						pauseTick++;
					}

					internalTick++;
					
					updateStageLogic();
				}
			}
			
			if(deltaSkip > 0)
			{
				deltaSkip -= Gdx.graphics.getDeltaTime();
				deltaSkip = Math.max(0, deltaSkip);
			}
			else
				updateStageDelta();

			if(profiling && !isOutOfGame() && !isPaused())
			{
				float ms = (System.nanoTime() - start) / 1000000f;
				System.out.println("Frame took: " + ms + "ms (Target @" + Gdx.graphics.getFramesPerSecond() + "fps = Max " + 1000f / Gdx.graphics.getFramesPerSecond() + "ms)");

				{
					for(String str : profilingOutput)
					{
						System.out.println(str);
					}

					System.out.println("NOTE! Vsync might delay frames, you should only look for lag that persists and causes fps drop.");
				}
			}
		}
	}

	@Override
	public boolean keyDown(int keycode)
	{
		KeyDownEvent event = new KeyDownEvent(keycode);
		
		callEvent(event);
		
		if(event.isCancelled())
			return true;
		
		if(!isOutOfGame())
		{
			boolean alt = Gdx.input.isKeyPressed(Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Keys.ALT_RIGHT);
			
			if(keycode == Keys.ESCAPE)
			{
				if(!isPaused())
					onPause();
				else
					onDePause();
			}
			else if(keycode == Input.Keys.S)
			{
				if(!Scheduler.isTracked("killScreen", "killScreen"))
				{
					for(StageObject obj : Game.getGame().getStageObjects())
					{
						if(obj instanceof LivingObject)
						{
							((LivingObject)obj).setHealth(0);
						}
					}

					Scheduler.trackMillis("killScreen", "killScreen", (long) 500);
				}
			}
			else if(keycode == Input.Keys.D)
			{
				setDebugMode(!isDebugMode());
			}
			else if(keycode == Input.Keys.F)
			{
				setProfiling(!isProfiling());
			}
			else if(alt && Gdx.input.isKeyJustPressed(Keys.ENTER))
			{
				Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), !Gdx.graphics.isFullscreen());
			}
		}
		
		return true;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return true;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return true;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return true;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return true;
	}
	
	private class ListenerData
	{
		public HashMap<Class, ArrayList<Method>> methodMap = new HashMap<Class, ArrayList<Method>>();
	}
	
	private HashMap<EventListener, ListenerData> eventListeners = new HashMap<EventListener, ListenerData>();
	
	public HashMap<EventListener, ListenerData> getEventListenersMap()
	{
		return eventListeners;
	}
	
	private class CallMethod
	{
		Method method;
		EventListener listener;
		EventHandler handler;
	}
	
	private Comparator<CallMethod> priorityComperator = new Comparator<CallMethod>()
	{
		@Override
		public int compare(CallMethod e1, CallMethod e2)
		{
			return e1.handler.priority().ordinal() - e2.handler.priority().ordinal();
		}
	};
	
	public void callEvent(Event event)
	{
		Array<CallMethod> methodsToCall = new Array<CallMethod>();
		
		for(Entry<EventListener, ListenerData> set : eventListeners.entrySet())
		{
			ListenerData data = set.getValue();
			EventListener l = set.getKey();
			
			for (Class<?> clazz = event.getClass(); Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass())
			{
				if(!data.methodMap.containsKey(clazz))
					continue;

				ArrayList<Method> methods = data.methodMap.get(clazz);

				for(Method m : methods)
				{
					EventHandler handler = m.getAnnotation(EventHandler.class);

					// Handler might be null here, but the registerEvents method should check for that.

					CallMethod call = new CallMethod();

					call.handler = handler;
					call.listener = l;
					call.method = m;

					methodsToCall.add(call);
				}
			}
		}
		
		methodsToCall.sort(priorityComperator);
		
		boolean cancellable = event instanceof ICancellable;
		ICancellable ican = (ICancellable) event;
		
		for(CallMethod m : methodsToCall)
		{
			if(cancellable)
			{
				if(ican.isCancelled() && m.handler.skipCancelled())
				{
					continue;
				}
			}
			
			try
			{
				m.method.invoke(m.listener, event);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Quite a heavy method, will use reflection through the event listener and read all callable event methods.
	 * A method becomes an event method when it meets these requirements:
	 * - Only has 1 argument, the event it's waiting for.
	 * - Has the @EventHandler annotation.
	 * @param listener
	 */
	public void registerEvents(EventListener listener)
	{
		ListenerData data = new ListenerData();
	
		for(Method m : listener.getClass().getDeclaredMethods())
		{
			EventHandler handler = m.getAnnotation(EventHandler.class);

			if(handler == null)
				continue;

			if(m.getParameterTypes().length != 1)
				continue;

			Class<?> clazz = m.getParameterTypes()[0];

			if(Event.class.isAssignableFrom(clazz))
			{
				m.setAccessible(true);

				if(!data.methodMap.containsKey(clazz))
				{
					data.methodMap.put(clazz, new ArrayList<Method>());
				}

				ArrayList<Method> methodList = data.methodMap.get(clazz);

				methodList.add(m);
			}
		}
		
		eventListeners.put(listener, data);
	}
	
	public void unregisterEvents(EventListener listener)
	{
		eventListeners.remove(listener);
	}
}
