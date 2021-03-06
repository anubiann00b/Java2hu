package java2hu.allstar;

import java.util.ArrayList;
import java.util.HashMap;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.allstar.backgrounds.BambooForestBG;
import java2hu.allstar.backgrounds.HokkaiBG;
import java2hu.allstar.backgrounds.MagicalStormBG;
import java2hu.allstar.backgrounds.MakaiBG;
import java2hu.allstar.backgrounds.MistLakeBG;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.enemies.day1.Cirno;
import java2hu.allstar.enemies.day1.Wakasagihime;
import java2hu.allstar.enemies.day2.Kagerou;
import java2hu.allstar.enemies.day2.Mokou;
import java2hu.allstar.enemies.day6.Sekibanki;
import java2hu.allstar.enemies.day7.Raiko;
import java2hu.allstar.enemies.day7.Seija;
import java2hu.allstar.enemies.day7.Sukuna;
import java2hu.allstar.enemies.day7.tsukumo.TsukumoGeneral;
import java2hu.allstar.enemies.day8.Byakuren;
import java2hu.allstar.enemies.day8.Day7Dialogue;
import java2hu.allstar.enemies.day8.Shinki;
import java2hu.allstar.enemies.day8.Shou;
import java2hu.allstar.enemies.day8.Yumeko;
import java2hu.allstar.enemies.day8.alice.AliceGeneral;
import java2hu.allstar.enemies.day8.getsus.GetsusGeneral;
import java2hu.allstar.enemies.day8.mai_yuki.MaiYukiGeneral;
import java2hu.allstar.enemies.day9.yumemi.Yumemi;
import java2hu.allstar.enemies.day9.yuuka.YuukaGeneral;
import java2hu.allstar.stage.MakaiStage;
import java2hu.background.bg3d.Background3D;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.overwrite.J2hObject;
import java2hu.util.Getter;

public class Days extends J2hObject
{
	private static HashMap<Integer, ArrayList<CharacterData>> days = new HashMap<Integer, ArrayList<CharacterData>>();
	
	static
	{
		final J2hGame game = Game.getGame();
		final float startX = game.getMinX() + (game.getMaxX() - game.getMinX()) / 2;
		final float startY = Game.getGame().getHeight() - 150;
		final float startHP = 100f;
		
		int day = 1;
		EnvironmentType type = EnvironmentType.MIST_LAKE;
		
		// Day 1
		{
			// Mist lake
			addCharacter(day, type, "Wakasagihime", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Wakasagihime boss = new Wakasagihime(startHP, startX, startY);
					return boss;
				}
			});
			
			addCharacter(day, type, "Cirno", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Cirno boss = new Cirno(startHP, startX, startY);
					return boss;
				}
			});
			
			// Letty
			// Three Faries
			// Hong Meiling
			
			type = EnvironmentType.ALT_DIM;
			
			// Alternative Dimension: (People who can't use danmaku can here!)
			// Maribel & Renko
			// Heida no Akyu
			// Rinnosuke
		}
		
		day++;
		type = EnvironmentType.BAMBOO;
		
		// Day 2
		{
			// Bamboo forest
			
			// Rumia
			// Wriggle
			// Mystia
			// Keine
			
			addCharacter(day, type, "Kagerou Imaizumi", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Kagerou boss = new Kagerou(startHP, startX, startY);
					return boss;
				}
			});
			
			// Tewi

			addCharacter(day, type, "Fujiwara No Mokou", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Mokou boss = Mokou.newInstance(startX, startY);
					return boss;
				}
			});
		}
		
		day++;
		type = EnvironmentType.UNDERGROUND_CITY;
		
		// Day 3
		{
			// Underground City:
			// Kisume
			// Yamame
			// Parsee
			// Satori
			// Yuugi
			// Rin
			// Utusho
			// Suika
			// Koishi
		}
		
		day++;
		type = EnvironmentType.YOUKAI_MOUNTAIN;
		
		// Day 4
		{
			// Youkai Mountain:
			// Aki Sisters
			// Hina
			// Nitori
			// Momiji
			// Hatate
			// Aya
			// Sanae
			// Kanako & Suwako
		}
		
		day++;
		type = EnvironmentType.ABOVE_THE_CLOUDS;
		
		// Day 5
		{
			// Above the clouds:
			// Kogasa
			// Nazrin
			// Lily White
			// Ichirin
			// Murusa
			// Prismriver Sisters
			// Iku
			// Nue
			// Tenshi
		}
		
		day++;
		type = EnvironmentType.CEMETRY_ROAD;
		
		// Day 6
		{
			// Cemetry road:
			// Kyouko
			
			addCharacter(day, type, "Sekibanki", new Getter<AllStarBoss>()
					{
						@Override
						public AllStarBoss get()
						{
							final Sekibanki boss = Sekibanki.newInstance(startX, startY);
							return boss;
						}
					});
			
			// Seiga & Zombie
			// Tojiko
			// Youmu
			// Monobe no Futo
			// Yuyuko
			// Miko
		}
		
		day++;
		type = EnvironmentType.THUNDER_CLOUDS;
		
		// Day 7
		{
			// Thundering clouds:
			addSpecial(day, type, "Yatsuhashi & Benben Tsukumo", new Getter<SpecialFlowScheme>()
			{
				@Override
				public SpecialFlowScheme get()
				{
					final TsukumoGeneral boss = new TsukumoGeneral();
					return boss;
				}
			});
			
			addCharacter(day, type, "Seija Kijin", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Seija boss = Seija.newInstance(startX, startY);
					return boss;
				}
			});
			
			addCharacter(day, type, "Sukuna Shinmyoumaru", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Sukuna boss = Sukuna.newInstance(startX, startY);
					return boss;
				}
			});

			addCharacter(day, type, "Raiko Horikawa", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Raiko raiko = Raiko.newInstance(startX, startY);

					return raiko;
				}
			});
			
			type = EnvironmentType.MOON;
			
			// The Moon:
			// Rei'sen (Reisen 2)
			// Reisen
			// Watatsuki's
			// Eirin
			// Kaguya
		}
		
		day++;
		type = EnvironmentType.MAKAI;
		
		// Day 8
		{
			addSpecial(day, EnvironmentType.MAKAI_STAGE, "Stage", new Getter<SpecialFlowScheme>()
			{
				@Override
				public SpecialFlowScheme get()
				{
					final MakaiStage stage = new MakaiStage();
					return stage;
				}
			});
			
			addSpecial(day, type, "Dialogue", new Day7Dialogue());
			
			addSpecial(day, type, "Mai & Yuki", new Getter<SpecialFlowScheme>()
			{
				@Override
				public SpecialFlowScheme get()
				{
					final MaiYukiGeneral boss = new MaiYukiGeneral(startX, startY);
					return boss;
				}
			});
			
			addCharacter(day, type, "Shou Toramaru", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Shou shou = Shou.newInstance(startX, startY);

					return shou;
				}
			});
			
			addCharacter(day, type, "Yumeko", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Yumeko yumeko = Yumeko.newInstance(startX, startY);

					return yumeko;
				}
			});
			
			addSpecial(day, type, "Alice Margatroid", new Getter<SpecialFlowScheme>()
			{
				@Override
				public SpecialFlowScheme get()
				{
					final AliceGeneral boss = new AliceGeneral(startX, startY);
					return boss;
				}
			});
			
			addCharacter(day, type, "Byakuren Hijiri", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Byakuren byakuren = Byakuren.newInstance(startX, startY);

					return byakuren;
				}
			});
			
			addCharacter(day, type, "Shinki", new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Shinki shinki = Shinki.newInstance(startX, startY, false);

					return shinki;
				}
			});
			
			addSpecial(day, type, "Gengetsu & Mugetsu", new Getter<SpecialFlowScheme>()
			{
				@Override
				public SpecialFlowScheme get()
				{
					final GetsusGeneral boss = new GetsusGeneral();
					return boss;
				}
			});
			
			type = EnvironmentType.SCARLET_DEVIL_MANSION;
			
			// SDM:
			// Patchouli
			// Sakuya
			// Remilia & Flandre
		}
		
		day++;
		type = EnvironmentType.FLOWER_FIELD;
		
		// Day 9
		{
			// Flower field:
			// Medicine
			
			addSpecial(day, type, "Yuuka Kazami", new Getter<SpecialFlowScheme>()
			{
				@Override
				public SpecialFlowScheme get()
				{
					final YuukaGeneral boss = new YuukaGeneral(startX, startY);
					return boss;
				}
			});
			
			// Komachi
			// Kasen Ibara
			// Chiyuri
			// Futatsuiwa
			
			addCharacter(day, type, Yumemi.FULL_NAME, new Getter<AllStarBoss>()
			{
				@Override
				public AllStarBoss get()
				{
					final Yumemi yumemi = new Yumemi(100f, startX, startY);

					return yumemi;
				}
			});
			
			// Shiki Eiki
		}
		
		day++;
		type = EnvironmentType.HAKURAI;
		
		// Day 10
		{
			// Hakurai Shrine/Border:
			// Ran & Chen
			// Sariel
			// Konngara
			// Marisa
			// Reimu
			// Yukari
			// Mima
		}
	}
	
	public static void addCharacter(int day, String name, Getter<AllStarBoss> getter)
	{
		addCharacter(day, EnvironmentType.NONE, name, getter);
	}
	
	public static void addCharacter(int day, EnvironmentType env, String name, Getter<AllStarBoss> getter)
	{
		ArrayList<CharacterData> map = getDay(day);
		
		if(map == null)
		{
			map = new ArrayList<CharacterData>();
			days.put(day, map);
		}
		
		CharacterData data = new CharacterData(name);
		data.bossGetter = getter;
		data.environment = env;
		
		map.add(data);
	}
	
	public static void addSpecialFight(int day, String name, Getter<SpecialFlowScheme> getter)
	{
		addSpecial(day, EnvironmentType.NONE, name, getter);
	}
	
	public static void addSpecial(int day, EnvironmentType env, String name, Getter<SpecialFlowScheme> getter)
	{
		ArrayList<CharacterData> map = getDay(day);
		
		if(map == null)
		{
			map = new ArrayList<CharacterData>();
			days.put(day, map);
		}
		
		CharacterData data = new CharacterData(name);
		data.specialGetter = getter;
		data.environment = env;
		
		map.add(data);
	}
	
	public static int[] getDays()
	{
		int[] array = new int[days.keySet().size()];
		
		int i = 0;
		
		for(Integer integer : days.keySet())
		{
			array[i] = integer;
			i++;
		}
		
		return array;
	}
	
	public static ArrayList<CharacterData> getDay(int day)
	{
		if(!days.containsKey(day))
			return null;
		
		ArrayList<CharacterData> map = days.get(day);
		
		return map;
	}
	
	public static class CharacterData extends J2hObject
	{
		public String name;
		public EnvironmentType environment;
		public Getter<AllStarBoss> bossGetter;
		public Getter<SpecialFlowScheme> specialGetter;
		
		public CharacterData(String name)
		{
			this.name = name;
		}
	}
	
	public static enum EnvironmentType
	{
		MIST_LAKE(new Getter<Background3D>()
		{
			@Override
			public Background3D get()
			{
				return new MistLakeBG();
			};
		}),
		ALT_DIM(null),
		BAMBOO(new Getter<Background3D>()
		{
			@Override
			public Background3D get()
			{
				return new BambooForestBG();
			};
		}, 30),
		UNDERGROUND_CITY(null),
		YOUKAI_MOUNTAIN(null),
		ABOVE_THE_CLOUDS(null),
		CEMETRY_ROAD(null),
		THUNDER_CLOUDS(new Getter<Background3D>()
		{
			@Override
			public Background3D get()
			{
				return new MagicalStormBG();
			}
		}),
		SCARLET_DEVIL_MANSION(null),
		MAKAI(new Getter<Background3D>()
		{
			@Override
			public Background3D get()
			{
				return new MakaiBG();
			};
		}),
		MAKAI_STAGE(new Getter<Background3D>()
		{
			@Override
			public Background3D get()
			{
				return new HokkaiBG();
			};
		}),
		MOON(null),
		FLOWER_FIELD(null),
		HAKURAI(null),
		EXTRA(null),
		NONE(null);

		private String name;
		private int spawnAnimationDelay;
		private Getter<Background3D> spawnEnvironment;
		
		private EnvironmentType(Getter<Background3D> spawnEnvironment)
		{
			this(spawnEnvironment, 0);
		}
		
		private EnvironmentType(Getter<Background3D> spawnEnvironment, int spawnAnimationDelay)
		{
			this.spawnEnvironment = spawnEnvironment;
			this.spawnAnimationDelay = spawnAnimationDelay;
			this.name = name();
		}
		
		public String getName()
		{
			return name;
		}
		
		public Getter<Background3D> getSpawnEnvironment()
		{
			return spawnEnvironment;
		}
		
		public int getSpawnAnimationDelay()
		{
			return spawnAnimationDelay;
		}
	}
}
