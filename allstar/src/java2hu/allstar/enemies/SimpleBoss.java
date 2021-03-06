package java2hu.allstar.enemies;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.spellcard.BossSpellcard;
import java2hu.system.SaveableObject;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class SimpleBoss extends AllStarBoss
{
	public final static String FULL_NAME = "";
	public final static String DATA_NAME = "";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	public SimpleBoss(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 24F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 24F, 5,6,7,8);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 9,10,11,12,12,12,12,12);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		
		setColor(new Color(0 / 255f, 0 / 255f, 0 / 255f, 1.0f));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Sprite bg = new Sprite(Loader.texture(FOLDER.child("bg.png")));
				Sprite bge = new Sprite(Loader.texture(FOLDER.child("bge.png")));
				
				// Set backgrounds sprite to the framebuffer of the boss aura to make use of the background bubbles.
			}
		};
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
	}

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final SimpleBoss boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, 60);

				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						bar.setObject(new CircleHealthBar(boss));
						
						g.spawn(boss);
						g.spawn(bar.getObject());
						
						bar.getObject().addSplit(0.8f);
						
						AllStarUtil.introduce(boss);
						
						boss.healUp();
						BossUtil.backgroundAura(boss);
						
						Game.getGame().startSpellCard(new NonSpell(boss));
					}
				}, 60);
			}
		}, 1);

		scheme.wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !boss.isOnStage();
			}
		});

		SchemeUtil.waitForDeath(scheme, boss);
		
		bar.getObject().split();
		boss.setHealth(boss.getMaxHealth());

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				game.clear(ClearType.ALL);
				
				backgroundSpawner.set(scheme.getBossAura());
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new Spell(boss));
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		scheme.doWait();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(boss);
				
				Game.getGame().clear(ClearType.ALL);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class NonSpell extends BossSpellcard<SimpleBoss>
	{	
		public NonSpell(SimpleBoss owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, SimpleBoss boss)
		{
			final Player player = game.getPlayer();
			
		}
	}

	public static class Spell extends BossSpellcard<SimpleBoss>
	{
		public Spell(SimpleBoss owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, SimpleBoss boss)
		{
			final Player player = game.getPlayer();
			
		}
	}
}

