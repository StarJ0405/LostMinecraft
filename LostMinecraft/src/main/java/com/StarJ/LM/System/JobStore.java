package com.StarJ.LM.System;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.StarJ.LM.Core;
import com.StarJ.LM.Items.WeaponItems;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.GUIStore.Group;
import com.StarJ.LM.System.JobStore.IdentityCalc.Calc;

public enum JobStore {
	// 전사
	WarLordJT("워로드:전투 태세", Group.warrior, Material.WHITE_BANNER, Skill.warrior.WarLord.values()) {
		@Override
		public double getMaxHealth() {
			return 250000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("실드 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.WarLordJT;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 998.59d;
		}

		private IdentityCalc[] idenCalc = new IdentityCalc[] {
				new IdentityCalc("전장의 방패 쉴드 증가량", Calc.MULIPLY, 1398.06d),
				new IdentityCalc("일반 스킬 피해 증가량", Calc.MULIPLY, 635.45d) };

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return idenCalc;
		}

		@Override
		public void setting(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			ds.setSkillGroupTypeDamage(Skill.warrior.WarLord.skillGroup.Normal, this.name(),
					idenCalc[0].getCalc(ds.getSpecialization()));
			ds.changeSetting(le);
		}

		@Override
		public void removeSetting(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			ds.setSkillGroupTypeDamage(Skill.warrior.WarLord.skillGroup.Normal, this.name(), 0);
			ds.changeSetting(le);
		}
	},
	WarLordGG("워로드:고독한 기사", Group.warrior, Material.BAMBOO, Skill.warrior.WarLord.values()) {
		@Override
		public double getMaxHealth() {
			return 250000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("???");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.WarLordGG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;
		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	BerserkerGB("버서커:광전사의 비기", Group.warrior, Material.TWISTING_VINES) {
		@Override
		public double getMaxHealth() {
			return 201700;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("분노 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BerserkerGB;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;
		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	BerserkerGG("버서커:광기", Group.warrior, Material.WEEPING_VINES) {
		@Override
		public double getMaxHealth() {
			return 201700;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("분노 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BerserkerGG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	DestroyerBM("디스트로이어:분노의 망치", Group.warrior, Material.IRON_AXE) {
		@Override
		public double getMaxHealth() {
			return 220400;
		}

		@Override
		public Identity getIdentity() {
			return new intIdentity("중력 코어");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.DestroyerBM;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	DestroyerJS("디스트로이어:중력 수련", Group.warrior, Material.NETHERITE_AXE) {
		@Override
		public double getMaxHealth() {
			return 220400;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("중력 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.DestroyerJS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	HolyKnightS("홀리나이트:심판자", Group.warrior, Material.NETHERITE_SWORD) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("신앙 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.HolyKnightS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	HolyKnightCA("홀리나이트:축복의 오라", Group.warrior, Material.IRON_SWORD) {
		@Override
		public double getMaxHealth() {
			return 277200;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("신앙 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.HolyKnightCA;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	// 무도가
	BattleMasterC("배틀마스터:초심", Group.martialArtist, Material.LEAD) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("미정");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BattleMasterC;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	BattleMasterAG("배틀마스터:오의 강화", Group.martialArtist, Material.NAME_TAG) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("엘리멘탈 버블", 4);
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BattleMasterAG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	InfighterCS("인파이터:체술", Group.martialArtist, Material.SCUTE) {
		@Override
		public double getMaxHealth() {
			return 210800;
		}

		@Override
		public Identity getIdentity() {
			return new infighterIdentity("기력/충격에너지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.InfighterCS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	InfighterCD("인파이터:충격단련", Group.martialArtist, Material.CONDUIT) {
		@Override
		public double getMaxHealth() {
			return 210800;
		}

		@Override
		public Identity getIdentity() {
			return new infighterIdentity("기력/충격에너지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.InfighterCD;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	SoulMasterYC("기공사:역천지체", Group.martialArtist, Material.SNOWBALL) {
		@Override
		public double getMaxHealth() {
			return 183800;
		}

		@Override
		public Identity getIdentity() {
			return new soulMasterIdentity("금강선공");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.SoulMasterYC;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	SoulMasterSM("기공사:세맥타통", Group.martialArtist, Material.CLAY_BALL) {
		@Override
		public double getMaxHealth() {
			return 183800;
		}

		@Override
		public Identity getIdentity() {
			return new soulMasterIdentity("금강선공");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.SoulMasterSM;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	LanceMasterJG("창술사:절정", Group.martialArtist, Material.BLAZE_ROD) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new lanceMasterIdentity("듀얼 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.WarLordJT;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	LanceMasterJJ("창술사:절제", Group.martialArtist, Material.TRIDENT) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("??");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.LanceMasterJJ;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	StrikerIP("스트라이커:일격필살", Group.martialArtist, Material.NETHERITE_BOOTS) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("엘리멘탈 버블", 4);
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.StrikerIP;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	StrikerON("스트라이커:오의난무", Group.martialArtist, Material.IRON_BOOTS) {
		@Override
		public double getMaxHealth() {
			return 192500;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("엘리멘탈 버블", 3);
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.StrikerON;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	// 헌터
	DevilHunterGM("데빌헌터:강화 무기", Group.hunter, Material.RAW_IRON) {
		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new devilGunslIdentity("퀵 스탠스", new String[] { "핸드건", "샷건", "라이플" });
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.DevilHunterGM;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	DevilHunterHG("데빌헌터:핸드거너", Group.hunter, Material.RAW_GOLD) {

		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("???");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.DevilHunterHG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	BlasterHG("블래스터:화력 강화", Group.hunter, Material.BLAZE_POWDER) {

		@Override
		public double getMaxHealth() {
			return 195600;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("화력 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BlasterHG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	BlasterPG("블래스터:포격 강화", Group.hunter, Material.FIRE_CHARGE) {

		@Override
		public double getMaxHealth() {
			return 166300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("포격 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BlasterPG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	HawkEyeDD("호크아이:두 번째 동료", Group.hunter, Material.BOW) {

		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("호크 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.HawkEyeDD;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	HawkEyeJS("호크아이:죽음의 습격", Group.hunter, Material.CROSSBOW) {

		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("호크 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.HawkEyeJS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	ScouterJU("스카우터:진화의 유산", Group.hunter, Material.GOLDEN_CARROT) {

		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("코어 에너지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ScouterJU;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	ScouterAG("스카우터:아르데타인의 기술", Group.hunter, Material.CARROT) {

		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("??");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.WarLordJT;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	GunslingerPM("건슬링어:피스메이커", Group.hunter, Material.AMETHYST_CLUSTER) {

		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new devilGunslIdentity("퀵 스탠스", new String[] { "핸드건", "샷건", "라이플" });
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.GunslingerPM;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	GunslingerSS("건슬링어:사냥의 시간", Group.hunter, Material.AMETHYST_SHARD) {

		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new devilGunslIdentity("퀵 스탠스", new String[] { "핸드건", "라이플" });
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.GunslingerSS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	// 마법사
	SummonerSS("서모너:상급 소환사", Group.mage, Material.HEART_OF_THE_SEA) {

		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("정령의 구슬", 7);
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.SummonerSS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	SummonerNG("서모너:넘치는 교감", Group.mage, Material.EVOKER_SPAWN_EGG) {

		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("???");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.SummonerNG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	ArcanaHH("아르카나:황후의 은총", Group.mage, Material.PAPER) {
		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new arcanaIdentity("카드");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ArcanaHH;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	ArcanaHJ("아르카나:황제의 칙령", Group.mage, Material.NETHER_STAR) {
		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new arcanaIdentity("카드");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ArcanaHJ;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	BardJG("바드:절실한 구원", Group.mage, Material.NOTE_BLOCK) {

		@Override
		public double getMaxHealth() {
			return 342000;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("세레나데");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BardJG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	BardJY("바드:진실된 용맹", Group.mage, Material.JUKEBOX) {

		@Override
		public double getMaxHealth() {
			return 142500;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("세레나데");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BardJY;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	SorceressJH("소서리스:점화", Group.mage, Material.BLAZE_ROD) {
		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("신비한 마력");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.SorceressJH;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	SorceressGR("소서리스:환류", Group.mage, Material.STICK) {
		@Override
		public double getMaxHealth() {
			return 158300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("신비한 마력");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.SorceressGR;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	// 암살자
	BladeJG("블레이드:잔재된 기운", Group.assassin, Material.IRON_SHOVEL) {
		@Override
		public double getMaxHealth() {
			return 183300;
		}

		@Override
		public Identity getIdentity() {
			return new bladeIdentity("블레이드 오브");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BladeJG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}

	},
	BladeB("블레이드:버스트", Group.assassin, Material.NETHERITE_SHOVEL) {
		@Override
		public double getMaxHealth() {
			return 183300;
		}

		@Override
		public Identity getIdentity() {
			return new bladeIdentity("블레이드 오브", "버스트 강화");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.BladeB;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	DemonicMC("데모닉:멈출 수 없는 충동", Group.assassin, Material.SHEARS) {
		@Override
		public double getMaxHealth() {
			return 166300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("잠식 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.DemonicMC;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	DemonicWE("데모닉:완벽한 억제", Group.assassin, Material.CHAIN) {
		@Override
		public double getMaxHealth() {
			return 166300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("잠식 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.DemonicWE;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	ReaperDS("리퍼:달의 소리", Group.assassin, Material.ARROW, Skill.assassin.Reaper.values()) {
		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("페르소나");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ReaperDS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 2330.09d;
		}

		private IdentityCalc[] idenCalc = new IdentityCalc[] { new IdentityCalc("급습 피해 증가량", Calc.MULIPLY, 2410.60d) };

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return idenCalc;
		}

		@Override
		public void setting(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			ds.setSkillGroupTypeDamage(Skill.assassin.Reaper.skillGroup.SurpiseAttack, this.name(),
					idenCalc[0].getCalc(ds.getSpecialization()));
			ds.changeSetting(le);
		}

		@Override
		public void removeSetting(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			ds.setSkillGroupTypeDamage(Skill.assassin.Reaper.skillGroup.SurpiseAttack, this.name(), 0);
			ds.changeSetting(le);
		}
	},
	ReaperGJ("리퍼:갈증", Group.assassin, Material.FEATHER) {
		@Override
		public double getMaxHealth() {
			return 150000;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("혼돈");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ReaperGJ;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	// 스페셜리스트
	ArtistMG("도화가:만개", Group.specialist, Material.INK_SAC) {
		@Override
		public double getMaxHealth() {
			return 342000;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("조화 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ArtistMG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	ArtistHG("도화가:회귀", Group.specialist, Material.GLOW_INK_SAC) {
		@Override
		public double getMaxHealth() {
			return 142500;
		}

		@Override
		public Identity getIdentity() {
			return new bubbleIdentity("조화 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.ArtistHG;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	MeteorologistJN("기상술사:질풍노도", Group.specialist, Material.FISHING_ROD) {
		@Override
		public double getMaxHealth() {
			return 166300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("빗방울 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.MeteorologistJN;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	MeteorologistIS("기상술사:이슬비", Group.specialist, Material.CLOCK) {
		@Override
		public double getMaxHealth() {
			return 166300;
		}

		@Override
		public Identity getIdentity() {
			return new percentIdentity("빗방울 게이지");
		}

		@Override
		public WeaponItems getWeapon() {
			return WeaponItems.MeteorologistIS;
		}

		@Override
		public double getMultiplyIdentity(double specialization) {
			return 1 + specialization / 1000d;

		}

		@Override
		public IdentityCalc[] getIdentityCalcs() {
			return null;

		}

		@Override
		public void setting(LivingEntity le) {
		}

		@Override
		public void removeSetting(LivingEntity le) {
		}
	},
	//
	;

	private final String displayName;
	private final Group group;
	private final Material type;
	private final Skill[] skills;

	private JobStore(String displayName, Group group, Material type, Skill... skills) {
		this.displayName = displayName;
		this.group = group;
		this.type = type;
		this.skills = skills;
	}

	public String getDisplayName() {
		return group.getColor() + displayName;
	}

	public Group getGroup() {
		return group;
	}

	public Material getType() {
		return type;
	}

	public Skill[] getSkills() {
		return skills;
	}

	public abstract WeaponItems getWeapon();

	public abstract Identity getIdentity();

	public abstract double getMaxHealth();

	public abstract double getMultiplyIdentity(double specialization);

	public double getMultiplyIdentity(LivingEntity le) {
		return getMultiplyIdentity(Core.getCore().getHashMapStore().getDataStore(le).getSpecialization());
	}

	public abstract void setting(LivingEntity le);

	public abstract void removeSetting(LivingEntity le);

	public abstract IdentityCalc[] getIdentityCalcs();

	public static class IdentityCalc {
		public enum Calc {
			MULIPLY, REDUCE
		}

		private final String displayName;
		private final Calc calc;
		private final double div;

		public IdentityCalc(String displayName, Calc calc, double div) {
			this.displayName = displayName;
			this.calc = calc;
			this.div = div;
		}

		public String getDisplayName() {
			return displayName;
		}

		public double getCalc(LivingEntity le) {
			return getCalc(Core.getCore().getHashMapStore().getDataStore(le).getSpecialization());
		}

		public double getCalc(double specialization) {
			if (calc.equals(Calc.MULIPLY))
				return 1 + specialization / this.div;
			else if (calc.equals(Calc.REDUCE))
				return 1 - specialization / this.div;
			return specialization;
		}

		public double getLore(double specialization) {
			return specialization / this.div;
		}
	}

	public static abstract class Identity {
		private final String display;
		protected final double max;
		protected double now;
		protected boolean lock;

		public Identity(String display) {
			this(display, 3000);
		}

		public Identity(String display, double max) {
			this.display = display;
			this.max = max;
			this.now = 0;
			this.lock = false;
		}

		public String getDisplay() {
			return display;
		}

		public double getNow() {
			return now;
		}

		public double getMax() {
			return max;
		}

		public void setLock(boolean lock) {
			this.lock = lock;
		}

		public void setNow(double now) {
			if (lock)
				return;
			this.now = now;
			if (this.now > this.max)
				this.now = this.max;
			else if (this.now < 0)
				this.now = 0;
		}

		public abstract String getActionbar();
	}

	public static class arcanaIdentity extends Identity {
		private final Skill[] cards;
		private final Skill[] slot;

		public arcanaIdentity(String display, Skill... cards) {
			super(display);
			this.cards = cards;
			this.slot = new Skill[2];
			this.now = 0;
		}

		public void draw() {
			Random r = new Random();
			if (slot[0] == null) {
				slot[0] = cards[r.nextInt(cards.length)];
				if (slot[0] == slot[1]) {
					slot[0] = null;
					draw();
					return;
				}
			} else {
				slot[1] = cards[r.nextInt(cards.length)];
				if (slot[0] == slot[1]) {
					slot[1] = null;
					draw();
					return;
				}
			}
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + (slot[0] != null ? slot[0].getDisplayName() : "없음") + ChatColor.WHITE + " / "
					+ (slot[1] != null ? slot[1].getDisplayName() : "없음");
		}
	}

	public static class devilGunslIdentity extends Identity {
		private final String[] status;
		private int now;

		public devilGunslIdentity(String display, String[] status) {
			super(display);
			this.status = status;
			this.now = 0;
		}

		public void changeStatus() {
			this.now++;
			if (now >= status.length)
				now = 0;
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + status[now];
		}
	}

	public static class infighterIdentity extends Identity {
		private double energy;// 기력
		private double impact;// 충격

		public infighterIdentity(String display) {
			super(display);
		}

		public double getImpact() {
			return impact;
		}

		public void setImpact(double impact) {
			this.impact = impact;
		}

		public double getEnergy() {
			return energy;
		}

		public void setEnergy(double energy) {
			this.energy = energy;
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + Math.round(energy / max * 100d) + " | " + Math.round(impact / max * 100d);
		}
	}

	public static class bladeIdentity extends Identity {
		private final int maxBubble;
		private final int maxStack;
		private int stack;
		private boolean status;
		private String[] display;

		public bladeIdentity(String... display) {
			super(display[0]);
			this.display = display;
			this.maxBubble = 3;
			this.maxStack = 20;
			this.stack = 0;
			this.status = false;
		}

		@Override
		public String getDisplay() {
			return !status ? display[0] : display[1];
		}

		public bladeIdentity(String display, double max) {
			super(display, max);
			this.maxBubble = 3;
			this.maxStack = 20;
			this.stack = 0;
			this.status = false;
		}

		@Override
		public String getActionbar() {
			if (status)
				return getDisplay() + " : " + stack + " / " + maxStack;
			else {
				int count = (int) (now * maxBubble / max);
				String bubble = "";
				for (int c = 0; c < maxBubble; c++)
					if (c < count)
						bubble += "●";
					else
						bubble += "○";
				return getDisplay() + " : " + bubble;
			}
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public void addStack() {
			this.stack++;
		}

		public void resetStack() {
			this.stack = 0;
		}
	}

	public static class lanceMasterIdentity extends Identity {
		private boolean status;

		public lanceMasterIdentity(String display) {
			super(display);
			status = false;
		}

		public lanceMasterIdentity(String display, double max) {
			super(display, max);
			status = false;
		}

		public void changeStatus() {
			status = !status;
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + Math.round(now / max * 100d) + "%";
		}
	}

	public static class soulMasterIdentity extends Identity {
		private int level;

		public soulMasterIdentity(String display) {
			super(display);
			this.level = 0;
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + now + "초";
		}

		public int getLevel() {
			return level;
		}

		public void nextLevel() {
			level++;
		}

		public void resetLevel() {
			level = 0;
		}
	}

	public static class bubbleIdentity extends Identity {
		private final int maxBubble;

		public bubbleIdentity(String display) {
			super(display);
			this.maxBubble = 3;
		}

		public bubbleIdentity(String display, int maxBubble) {
			super(display);
			this.maxBubble = maxBubble;
		}

		public bubbleIdentity(String display, double max, int maxBubble) {
			super(display, max);
			this.maxBubble = maxBubble;
		}

		@Override
		public String getActionbar() {
			int count = (int) (now * maxBubble / max);
			String bubble = "";
			for (int c = 0; c < maxBubble; c++)
				if (c < count)
					bubble += "●";
				else
					bubble += "○";
			return getDisplay() + " : " + bubble;
		}
	}

	public static class intIdentity extends Identity {
		public intIdentity(String display) {
			super(display);
		}

		public intIdentity(String display, double max) {
			super(display, max);
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + Math.round(now) + " / " + Math.round(max);
		}
	}

	public static class percentIdentity extends Identity {
		public percentIdentity(String display) {
			super(display);
		}

		public percentIdentity(String display, double max) {
			super(display, max);
		}

		@Override
		public String getActionbar() {
			return getDisplay() + " : " + Math.round(now / max * 100d) + "%";
		}
	}

}
