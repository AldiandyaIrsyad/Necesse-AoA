package AspectsofAlteration;

import AspectsofAlteration.AoAContent.*;
import AspectsofAlteration.AoAContent.AoABiomes.*;
import AspectsofAlteration.AoAContent.AoABiomes.AoADungeons.AoAVoidBiome;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.*;
import AspectsofAlteration.AoAContent.AoABuffs.*;
import AspectsofAlteration.AoAContent.AoAFlora.*;
import AspectsofAlteration.AoAContent.AoALevelEvents.*;
import AspectsofAlteration.AoAContent.AoAMaterials.*;
import AspectsofAlteration.AoAContent.AoAMobs.*;
import AspectsofAlteration.AoAContent.AoAMobs.AoABosses.*;
import AspectsofAlteration.AoAContent.AoAProjectiles.*;
import AspectsofAlteration.AoAContent.AoATiles.*;
import AspectsofAlteration.AoAContent.AoAWeapons.*;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.*;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.ShownItemCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SimpleSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff;
import necesse.entity.projectile.QueenSpiderEggProjectile;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.item.placeableItem.tileItem.GrassSeedItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.item.toolItem.axeToolItem.CustomAxeToolItem;
import necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.CustomBowProjectileToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CustomSpearToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CustomSwordToolItem;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.*;
import necesse.level.gameObject.furniture.*;
import necesse.level.gameTile.PathTiledTile;
import necesse.level.gameTile.SimpleFloorTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

import java.awt.*;

import static necesse.engine.registries.BiomeRegistry.registerBiome;
import static necesse.engine.registries.ItemRegistry.registerItem;
import static necesse.engine.registries.ObjectRegistry.getObject;
import static necesse.engine.registries.ObjectRegistry.registerObject;
import static necesse.engine.registries.RecipeTechRegistry.registerTech;

@ModEntry
public class AoA {

    public void init() {
        System.out.println("Hello and Welcome to Aspects of Alteration!");

        // Register our tiles
        TileRegistry.registerTile("aoavoid", new AoAVoid(),1, true);
        TileRegistry.registerTile("aoaprecursorplating", new PathTiledTile("aoaprecursorplating", new Color(65, 65, 65)), 200.0F, true);
        TileRegistry.registerTile("aoaprecursorfloor", new SimpleFloorTile("aoaprecursorfloor", new Color(33, 33, 33)), 200.0F, true);
        TileRegistry.registerTile("aoaroughtfloorplate", new PathTiledTile("aoaroughtfloorplate", new Color(150, 150, 150)), 5.0F, true);
        TileRegistry.registerTile("aoaroughtconncectingfloorplate", new PathTiledTile("aoaroughtconncectingfloorplate", new Color(162, 162, 162)), 5.0F, true);
        TileRegistry.registerTile("aoasmoothbrick", new PathTiledTile("aoasmoothbrick", new Color(150, 150, 150)), 5.0F, true);
        TileRegistry.registerTile("aoasmoothdoublebrick", new PathTiledTile("aoasmoothdoublebrick", new Color(162, 162, 162)), 5.0F, true);
        TileRegistry.registerTile("aoasteelfactorytile", new SimpleFloorTile("aoasteelfactorytile", new Color(129, 128, 128)), 2.0F, true);
        TileRegistry.registerTile("aoasmallovergrownstonebricks", new SimpleFloorTile("aoasmallovergrownstonebricks", new Color(168, 215, 171)), 2.0F, true);
        TileRegistry.registerTile("aoasmallmossystonebricks", new SimpleFloorTile("aoasmallmossystonebricks", new Color(105, 114, 87)), 2.0F, true);
        TileRegistry.registerTile("aoasmallstonebricks", new SimpleFloorTile("aoasmallstonebricks", new Color(162, 162, 162)), 2.0F, true);
        TileRegistry.registerTile("aoaconcretepath", new PathTiledTile("aoaconcretepath", new Color(70, 70, 70)), 5.0F, true);
        TileRegistry.registerTile("aoabasaltfinetileddarkfloor", new SimpleFloorTile("aoabasaltfinetileddarkfloor", new Color(66, 66, 66)), 2.0F, true);
        TileRegistry.registerTile("aoabasaltfinetiledfloor", new SimpleFloorTile("aoabasaltfinetiledfloor", new Color(65, 65, 66)), 2.0F, true);
        TileRegistry.registerTile("aoabasaltslabtile", new SimpleFloorTile("aoabasaltslabtile", new Color(65, 65, 66)), 2.0F, true);
        TileRegistry.registerTile("aoabasalttiledfloor", new SimpleFloorTile("aoabasalttiledfloor", new Color(65, 65, 66)), 2.0F, true);
        TileRegistry.registerTile("aoabasaltbrightslab", new PathTiledTile("aoabasaltbrightslab", new Color(70, 70, 70)), 5.0F, true);
        TileRegistry.registerTile("aoabasaltpolishedfloor", new PathTiledTile("aoabasaltpolishedfloor", new Color(70, 70, 70)), 5.0F, true);
        TileRegistry.registerTile("aoasteelfactorypathtile", new PathTiledTile("aoasteelfactorypathtile", new Color(70, 70, 70)), 5.0F, true);
        TileRegistry.registerTile("aoastonebrickslab", new SimpleFloorTile("aoastonebrickslab", new Color(103, 103, 103)), 2.0F, true);

        TileRegistry.registerTile("aoabloomingstonetile", new SimpleFloorTile("aoabloomingstonetile", new Color(108, 108, 108)), 200.0F, true);
        TileRegistry.registerTile("aoacrystalsandtile", new SimpleFloorTile("aoacrystalsandtile", new Color(125, 173, 168)), 2.0F, true);
        TileRegistry.registerTile("aoacrystalstonetile", new SimpleFloorTile("aoacrystalstonetile", new Color(58, 80, 77)), 2.0F, true);
        TileRegistry.registerTile("aoamagentamosstile", new AoAMagentaMossGrassTile(), 1, false);
        TileRegistry.registerTile("aoagoldenmosstile", new AoAGoldenMossGrassTile(), 1, false);
        TileRegistry.registerTile("aoacyanmosstile", new AoACyanMossGrassTile(), 1, false);
        TileRegistry.registerTile("aoamosstile", new SimpleFloorTile("aoamosstile", new Color(69, 80, 58)), 2.0F, true);
        TileRegistry.registerTile("aoaantspidernesttile", new AoAAntSpiderNestTile(), 1, false);
        TileRegistry.registerTile("aoabogstonetile", new SimpleFloorTile("aoabogstonetile", new Color(45, 41, 41)), 2.0F, true);
        TileRegistry.registerTile("aoaboggrasstile", new AoABogGrassTile(), 1, false);
        TileRegistry.registerTile("aoaashengrasstile", new AoAAshenGrassTile(), 1, false);
        TileRegistry.registerTile("aoaalpinegrasstile", new AoAAlpineGrass(), 1, false);
        TileRegistry.registerTile("aoafrostbornsnowtile", new AoAFrostbornSnowTile(), 1, true);
        TileRegistry.registerTile("aoabasaltstonetile", new AoABasaltStoneTile(), 1, false);
        TileRegistry.registerTile("aoavenomstonetile", new AoAVenomStoneTile(), 1, false);
        TileRegistry.registerTile("aoavenommudtile", new AoAVenomStoneTile(), 1, false);

        // Register out objects
        AoAInventorsTable.registerInventorsTable();
        AoAInventorsTableTier2.registerInventorsTable();
        AoAInventorsTableTier3.registerInventorsTable();
        AoAInventorsTableTier4.registerInventorsTable();
        AoAInventorsTableTier5.registerInventorsTable();
        AoAInventorsTableTier6.registerInventorsTable();
        AoASmithyTier1.registerSmithyTier1();
        AoASmithyTier2.registerSmithyTier2();
        AoASmithyTier3.registerSmithyTier3();
        AoASmithyTier4.registerSmithyTier4();
        AoASmithyTier5.registerSmithyTier5();
        AoABarrelStackObject.registerBarrelStackObject();
        AoAIndustrialGrinderObject.registerIndustrialGrinderObject();
        AoAAdvancedGrinderObject.registerAdvancedGrinderObject();
        int[] darkstoneWallIDs = WallObject.registerWallObjects("aoadarkstone", "aoadarkstonewall", 0, new Color(72, 98, 66), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoadarkstone = (WallObject)getObject(darkstoneWallIDs[0]);
        int[] darkstoneglassWallIDs = WallObject.registerWallObjects("aoadarkstoneglass", "aoadarkstoneglasswall", 0, new Color(72, 98, 66), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoadarkstoneglass = (WallObject)getObject(darkstoneglassWallIDs[0]);
        int[] darkstonewoodwindowWallIDs = WallObject.registerWallObjects("aoadarkstonewoodwindow", "aoadarkstonewoodwindow", 0, new Color(72, 98, 66), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoadarkstonewoodwindow = (WallObject)getObject(darkstonewoodwindowWallIDs[0]);
        int[] aoaancientWallIDs = WallObject.registerWallObjects("aoaancient", "aoaancientwall", 0, new Color(133, 133, 133), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoaancient = (WallObject)getObject(aoaancientWallIDs[0]);
        int[] aoamossWallIDs = WallObject.registerWallObjects("aoamoss", "aoamosswall", 0, new Color(53, 65, 50), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoamoss = (WallObject)getObject(aoamossWallIDs[0]);
        int aoaGlassFenceID = registerObject("aoaglassfence", new FenceObject("aoaglassfence", new Color(170, 255, 236), 12, 10), 2.0F, true);
        FenceGateObject.registerGatePair(aoaGlassFenceID, "aoaglassfencegate", "aoaglassfencegate", new Color(169, 253, 235), 12, 10, 4.0F);
        int aoachainlinkFenceID = registerObject("aoachainlinkfence", new FenceObject("aoachainlinkfence", new Color(134, 134, 134), 12, 10), 2.0F, true);
        FenceGateObject.registerGatePair(aoachainlinkFenceID, "aoachainlinkgate", "aoachainlinkgate", new Color(133, 133, 134), 12, 10, 4.0F);
        int[] aoabasaltWallsID = WallObject.registerWallObjects("aoabasalt", "aoabasaltwall", 0, new Color(87, 87, 87), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoabasalt = (WallObject)getObject(aoabasaltWallsID[0]);
        int[] aoachiseledbasaltWallsID = WallObject.registerWallObjects("aoachiseledbasalt", "aoachiseledbasaltwall", 0, new Color(87, 87, 87), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoachiseledbasalt = (WallObject)getObject(aoachiseledbasaltWallsID[0]);
        int[] aoadelapitatedwallWallsID = WallObject.registerWallObjects("aoadelapitated", "aoadelapitatedwall", 0, new Color(99, 112, 89), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoadelapitated = (WallObject)getObject(aoadelapitatedwallWallsID[0]);
        int[] aoapolishedsteelwallWallsID = WallObject.registerWallObjects("aoapolishedsteel", "aoapolishedsteelwall", 0, new Color(128, 156, 157), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoapolishedsteel = (WallObject)getObject(aoapolishedsteelwallWallsID[0]);
        int[] aoacastlebrickWallsID = WallObject.registerWallObjects("aoacastlebrick", "aoacastlebrick", 0, new Color(128, 156, 157), ToolType.ALL, 2.0F, 6.0F);
        WallObject aoacastlebrick = (WallObject)getObject(aoacastlebrickWallsID[0]);
        int aoasimpleironFenceID = registerObject("aoasimpleironfence", new FenceObject("aoasimpleironfence", new Color(134, 134, 134), 12, 10), 2.0F, true);
        int aoasimpleredironFenceID = registerObject("aoasimpleredironfence", new FenceObject("aoasimpleredironfence", new Color(159, 115, 115), 12, 10), 2.0F, true);
        int aoasimplegreenironFenceID = registerObject("aoasimplegreenironfence", new FenceObject("aoasimplegreenironfence", new Color(76, 94, 74), 12, 10), 2.0F, true);
        int aoahedgeFenceID = registerObject("aoahedgefence", new FenceObject("aoahedgefence", new Color(90, 143, 62), 12, 10), 2.0F, true);

        RockObject aoabasaltstone;
        RockObject aoadeadlandbogstone;

        registerObject("aoabasaltstone", aoabasaltstone = new RockObject("aoabasaltstone", new Color(62, 50, 48), "aoabasaltrockmaterial"), 0.0F, false);
        aoabasaltstone.toolTier = 3;
        registerObject("aoarawignusorebasalt", new RockOreObject(aoabasaltstone, "oremask", "aoarawignusshardore", new Color(211, 93, 8), "aoarawignusshard"), 0.0F, false);
        registerObject("aoamineralchunkorebasalt", new RockOreObject(aoabasaltstone, "oremask", "aoamineralchunkore", new Color(93, 75, 24), "aoamineralchunk"), 0.0F, false);
        registerObject("aoaironbasalt", new RockOreObject(aoabasaltstone, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
        registerObject("aoacopperbasalt", new RockOreObject(aoabasaltstone, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
        registerObject("aoagoldbasalt", new RockOreObject(aoabasaltstone, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
        registerObject("aoatungstenbasalt", new RockOreObject(aoabasaltstone, "oremask", "tungstenore", new Color(40, 49, 57), "tungstenore"), 0.0F, false);
        registerObject("aoalifequartzbasalt", new RockOreObject(aoabasaltstone, "oremask", "lifequartzore", new Color(180, 50, 61), "lifequartz", 1, 1), 0.0F, false);

        registerObject("aoadeadlandbogrock", aoadeadlandbogstone = new RockObject("aoadeadlandbogrock", new Color(72, 65, 55), "aoadeadlandrockmaterial"), 0.0F, false);
        aoadeadlandbogstone.toolTier = 3;
        registerObject("aoasyrilliteoredeadlandstone", new RockOreObject(aoadeadlandbogstone, "oremask", "aoasyrilliteore", new Color(80, 126, 43), "aoasyrilliteore"), 0.0F, false);
        registerObject("aoadeadlandbogrockiron", new RockOreObject(aoadeadlandbogstone, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
        registerObject("aoadeadlandbogrockcopper", new RockOreObject(aoadeadlandbogstone, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
        registerObject("aoadeadlandbogrockgold", new RockOreObject(aoadeadlandbogstone, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
        registerObject("aoadeadlandbogrockgoldivy", new RockOreObject(aoadeadlandbogstone, "oremask", "ivyore", new Color(91, 130, 36), "ivyore"), 0.0F, false);
        registerObject("aoadeadlandbogrocktungsten", new RockOreObject(aoadeadlandbogstone, "oremask", "tungstenore", new Color(40, 49, 57), "tungstenore"), 0.0F, false);
        registerObject("aoadeadlandbogrocklifequartz", new RockOreObject(aoadeadlandbogstone, "oremask", "lifequartzore", new Color(180, 50, 61), "lifequartz", 1, 1), 0.0F, false);
        registerObject("aoadeadlandbogrockfloritite", new RockOreObject(aoadeadlandbogstone, "oremask", "aoaflorititeore", new Color(69, 129, 53), "aoaflorititeore", 1, 1), 0.0F, false);


        registerObject("aoavenomstalk", new AoAVenomStalk(), 2, false);
        registerObject("aoarefrigerator", new AoARefrigeratorObject("aoarefrigerator", 80, new Color(159, 180, 196)), 10.0F, true);
        registerObject("aoatier1extractor", new AoATier1Extractor(), 2.0F, true);
        registerObject("aoatier2extractor", new AoATier2Extractor(), 2.0F, true);
        registerObject("aoadeepstoneforge", new AoATier2ProcessingForgeObject(), 2.0F, true);
        registerObject("aoafallenforge", new AoATier3ProcessingForgeObject(), 2.0F, true);
        registerObject("aoafusionforge", new AoATier4ProcessingForgeObject(), 2.0F, true);
        registerObject("aoarootresin", new AoARootResinPlant(), 5.0F, true);
        registerObject("aoadecorationtableobject", new AoADecorationTableObject(), 10.0F, true);
        registerObject("aoasimplegrinderobject", new AoASimpleGrinderObject(), 10.0F, true);
        registerObject("aoademonicgrinderobject", new AoADemonicGrinderObject(), 10.0F, true);
        registerObject("aoagoldenmossplantgrassobject", new AoAGoldenMossPlantGrassObject(), 0.0F, false);
        registerObject("aoamagentamossplantgrassobject", new AoAMagentaMossPlantGrassObject(), 0.0F, false);
        registerObject("aoacyanmossplantgrassobject", new AoACyanMossPlantGrassObject(), 0.0F, false);
        registerObject("aoalegendaryswordrock", new AoALegendarySwordRock(), 2, false);
        registerObject("aoaspiderantegg", new AoASpiderAntEgg(), 2, false);
        registerObject("aoaspideranteggcluster", new AoASpiderAntEggCluster(), 2, false);
        registerObject("aoaspideranteggclustersmall", new AoASpiderAntEggClusterSmall(), 2, false);
        registerObject("aoapetrifiedtree", new TreeObject("aoapetrifiedtree", "stone", "stone", new Color(47, 43, 30), 42, 42, 70, null), 0.0F, false);
        registerObject("aoatallboggrassobject", new AoATallBogGrassObject(), 2, false);
        registerObject("aoavolcanicwatergrass", new WaterPlantObject("aoavolcanicwatergrass", 32, new Color(229, 81, 14)), 1.0F, true);
        registerObject("aoavolcanograssobject", new AoAVolcanoGrassObject(), 2, false);
        registerObject("aoacindersapling", new AoATreeSaplingObject("aoacindersapling", "aoavolcanotree", 1300, 2200, true), 5.0F, true);
        registerObject("aoavolcanotree", new TreeObject("aoavolcanotree", "aoacinderlog", "aoacindersapling", new Color(211, 82, 18), 42, 42, 70, "aoavolcanoleaves"), 0.0F, false);
        registerObject("aoaashtree", new TreeObject("aoaashtree", "aoacinderlog", "aoacindersapling", new Color(47, 43, 30), 42, 42, 70, "deadwoodleaves"), 0.0F, false);
        registerObject("aoafrostborngrass", new AoAFrostbornGrassObject(), 2, false);
        registerObject("aoafrostbornsapling", new AoATreeSaplingObject("aoafrostbornsapling", "aoafrostborntree", 1800, 2700, true), 5.0F, true);
        registerObject("aoagiantfrostbornsapling", new AoATreeSaplingObject("aoagiantfrostbornsapling", "aoagiantfrostborntree", 2800, 3700, true), 5.0F, true);
        registerObject("aoagiantfrostborntree", new AoAGiantTree("aoagiantfrostborntree", "pinelog", "aoagiantfrostbornsapling",new Color(90, 83, 71), 32, 60, 120, "aoafrostbornleaves"), 0.0F, false);
        registerObject("aoafrostborntree", new TreeObject("aoafrostborntree", "pinelog", "aoafrostbornsapling", new Color(86, 69, 40), 32, 60, 120, "aoafrostbornleaves"), 0.0F, false);
        registerObject("aoapinetree", new TreeObject("pinetree", "pinelog", "aoapinesapling", new Color(86, 69, 40), 32, 60, 120, "pineleaves"), 0.0F, false);
        registerObject("aoapinesapling", new AoATreeSaplingObject("aoapinesapling", "aoapinetree", 1800, 2700, true), 5.0F, true);
        registerObject("aoagiantpinesapling", new AoATreeSaplingObject("aoagiantpinesapling", "aoagiantpinetree", 2800, 3700, true), 5.0F, true);
        registerObject("aoaalpineshrub", new AoAAlpineShrubObject(), 0.0F, true);
        registerObject("aoaalpinebush", new AoAAlpineBushObject(), 0.0F, true);
        registerObject("aoaalpinegrassobject", new AoAAlpineGrassObject(), 2, false);
        registerObject("aoamushroomgrassobject", new AoAMushroomGrassObject(), 2.0F, false);
        registerObject("aoatallancientgrass", new AoATallAncientGrass(), 0.0F, false);
        registerObject("aoashrubbushobject", new AoAShrubBushObject(), 0.0F, true);
        registerObject("aoabushobject", new AoABushObject(), 0.0F, true);
        registerObject("aoaignuscluster", new AoAIgnusCluster(), 0.0F, true);
        registerObject("aoaignusclustersmall", new AoAIgnusClusterSmall(), 0.0F, true);
        registerObject("aoacrystalcluster", new AoACrystalCluster(), 0.0F, true);
        registerObject("aoacrystalclustersmall", new AoACrystalClusterSmall(), 0.0F, true);
        registerObject("aoaaurumarboristree", new AoAAurumArborisTree("aoaaurumarboris", "aoaaurumarborislog", null,new Color(140, 83, 71), 32, 60, 120, "aoaaurumarborisleaves"), 0.0F, false);
        registerObject("aoagiantpinetree", new AoAGiantTree("aoagiantpinetree", "pinelog", "aoagiantpinesapling",new Color(90, 83, 71), 32, 60, 120, "pineleaves"), 0.0F, false);
        registerObject("aoadarkstonemodulartable", new ModularTableObject("aoadarkstonemodulartable", new Color(69, 89, 66)), 10.0F, true);
        registerObject("aoalabmodulartable", new ModularTableObject("aoalabmodulartable", new Color(101, 85, 58)), 10.0F, true);
        registerObject("aoabigstreetlightobject", new AoALightPostObject("aoabigstreetlightobject", null , new Color(243, 156, 54),30.0F , 0.2F), 10.0F, true);
        registerObject("aoastreetlightobject", new AoALightPostObject("aoastreetlightobject", null , new Color(241, 155, 55),30.0F , 0.2F), 10.0F, true);
        registerObject("aoaoakbarrelstand", new DeskObject("aoaoakbarrelstand",new Color(133, 116, 99)), 10.0F, true);
        registerObject("aoaoakcrate", new StorageBoxInventoryObject("aoaoakcrate", 40, new Color(178, 118, 75)), 20.0F, true);
        registerObject("aoasprucecrate", new StorageBoxInventoryObject("aoasprucecrate", 40, new Color(98, 68, 45)), 20.0F, true);
        registerObject("aoapinecrate", new StorageBoxInventoryObject("aoapinecrate", 40, new Color(77, 52, 47)), 20.0F, true);
        registerObject("aoawillowcrate", new StorageBoxInventoryObject("aoawillowcrate", 40, new Color(121, 97, 85)), 20.0F, true);
        registerObject("aoapalmcrate", new StorageBoxInventoryObject("aoapalmcrate", 40, new Color(196, 165, 137)), 20.0F, true);
        registerObject("aoawalllightlow", new AoAWallLightLowObject(), 2.0F, true);
        registerObject("aoawalllight", new AoAWallLightObject(), 2.0F, true);
        BathtubObject.registerBathtub("aoamodernbathtub", "aoamodernbathtub", new Color(206, 206, 206), 10.0F);
        registerObject("aoamoderntoilet", new ToiletObject("aoamoderntoilet", new Color(206, 206, 206)), 5.0F, true);
        DinnerTableObject.registerDinnerTable("aoamoderndinnertable", "aoamoderndinnertable", new Color(206, 206, 206), 20.0F);
        BedObject.registerBed("aoamodernbed", "aoamodernbed", new Color(206, 206, 206), 100.0F);
        registerObject("aoamoderndresser", new DresserObject("aoamoderndresser", new Color(206, 206, 206)), 10.0F, true);
        registerObject("aoamodernbookshelf", new BookshelfObject("aoamodernbookshelf", new Color(206, 206, 206)), 10.0F, true);
        registerObject("aoasteelbookshelf", new BookshelfObject("aoasteelbookshelf", new Color(100, 100, 100)), 10.0F, true);
        registerObject("aoamoderncabinet", new CabinetObject("aoamoderncabinet", new Color(206, 206, 206)), 10.0F, true);
        DinnerTableObject.registerDinnerTable("aoalabtablefluids", "aoalabtablefluids", new Color(168, 158, 157), 20.0F);
        DinnerTableObject.registerDinnerTable("aoalabtablechems", "aoalabtablechems", new Color(168, 158, 157), 20.0F);
        DinnerTableObject.registerDinnerTable("aoalabtable", "aoalabtable", new Color(168, 158, 157), 20.0F);
        registerObject("aoalabofficechair", new ChairObject("aoalabofficechair", new Color(168, 158, 157)), 5.0F, true);
        registerObject("aoavinewallobject", new AoADecorationObject("aoavinewallobject", new Color(0x755C3B)), 10.0F, true);
        registerObject("aoagrownvinewallobject", new AoADecorationObject("aoagrownvinewallobject", new Color(0x4E753B)), 10.0F, true);

        registerObject("wildaoafrostthistleplant", new CustomWildFlowerObject("aoafrostthistle", 5, "aoafrostthistleseed", "aoafrostthistleplant", 7, new Color(150, 205, 245), new String[]{"snowtile"}), 0.0F, false);
        registerObject("aoafrostthistle", (new FlowerObject("aoafrostthistle", 6, 10000, 960, new Color(150, 205, 245))).addGlobalIngredient(new String[]{"anycoolingfuel"}), 3.0F, true);
        SeedObject.registerSeedObjects("aoafrostthistleseed", "aoafrostthistle", "aoafrostthistleplant", 24, 0, 5, 500.0F, 1500.0F, new Color(149, 243, 244), 1.0F);

        // Register our biomes
        registerBiome("aoabanefulmarsh", new AoABanefulMarshBiome(), 80, "aoabanefulmarsh");
        registerBiome("aoavoidbiome", new AoAVoidBiome(), 0, "aoavoidbiome");
        registerBiome("aoaoceanbiome", new AoAOceanBiome(), 90, "aoaocean");
        registerBiome("aoacrystal", new AoACrystalBiome(), 90, "aoacrystal");
        registerBiome("aoadeadlandbog", new AoADeadlandBogBiome(), 90, "aoadeadlandbog");
        registerBiome("aoavolcanic", new AoAVolcanicBiome(), 90, "aoavolcanic");
        registerBiome("aoafrostburned", new AoAFrostburnedRuinsBiome(), 90, "aoafrostburned");
        registerBiome("aoaancientforest", new AoAAncientForestBiome(), 90, "aoaancientforest");
        registerBiome("aoaalpine", new AoAAlpineBiome(), 90, "aoaalpine");

        // Register our Tech
        registerTech("aoatier1extractor");
        registerTech("aoafusion");
        registerTech("aoadecorationtable");
        registerTech("aoasimplegrinder");
        registerTech("aoademonicgrinder");
        registerTech("aoaadvancedgrinder");
        registerTech("aoaindustrialgrinder");
        registerTech("aoainventorstable");
        registerTech("aoainventorstabletier2");
        registerTech("aoainventorstabletier3");
        registerTech("aoainventorstabletier4");
        registerTech("aoainventorstabletier5");
        registerTech("aoainventorstabletier6");
        registerTech("aoasmithytier1");
        registerTech("aoasmithytier2");
        registerTech("aoasmithytier3");
        registerTech("aoasmithytier4");
        registerTech("aoasmithytier5");

        // Register our items
        registerItem("aoamythrilhelmet", new SetHelmetArmorItem(25, 1000, Item.Rarity.RARE, "aoamythrilhelmet", "aoaantplatescalechestplate", "aoaantplatescaleboots", "aoaantplatescalesetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.10F), new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoabanditsmask", new SetHelmetArmorItem(25, 1000, Item.Rarity.RARE, "aoabanditsmask", "aoaantplatescalechestplate", "aoaantplatescaleboots", "aoaantplatescalesetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.20F), new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.20F), new ModifierValue(BuffModifiers.MELEE_CRIT_CHANCE, 0.15F)});
            }
        }, 110.0F, true);
        registerItem("aoaprotectionsuithelmet", new SetHelmetArmorItem(15, 1000, Item.Rarity.LEGENDARY, "aoaprotectionsuithelmet", "aoaprotectionsuitchest", "aoaprotectionsuitboots", "aoaprotectionsuitsetbonusbuff") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.03F)});
            }
        }, 110.0F, true);
        registerItem("aoaprotectionsuitchest", new ChestArmorItem(22, 1000, Item.Rarity.LEGENDARY, "aoaprotectionsuitchest", "aoaprotectionsuitarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.04F)});
            }
        }, 160.0F, true);
        registerItem("aoaprotectionsuitboots", new BootsArmorItem(13, 1000, Item.Rarity.LEGENDARY, "aoaprotectionsuitboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.03F)});
            }
        }, 80.0F, true);
        registerItem("aoamalachitehelmet", new SetHelmetArmorItem(22, 1000, Item.Rarity.LEGENDARY, "aoamalachitehelmet", "aoamalachitechestplate", "aoamalachiteboots", "aoamalachitesetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, -0.01F)});
            }
        }, 110.0F, true);
        registerItem("aoamalachitechestplate", new ChestArmorItem(28, 1000, Item.Rarity.LEGENDARY, "aoamalachitechestplate", "aoamalachitearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, -0.02F)});
            }
        }, 160.0F, true);
        registerItem("aoamalachiteboots", new BootsArmorItem(20, 1000, Item.Rarity.LEGENDARY, "aoamalachiteboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, -0.01F)});
            }
        }, 80.0F, true);
        registerItem("aoasteelplatehelmet", new SetHelmetArmorItem(10, 1000, Item.Rarity.UNCOMMON, "aoasteelplatehelmet", "aoasteelplatechest", "aoasteelplateboots", "aoasteelsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, -0.02F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoasteelplatechest", new ChestArmorItem(12, 1000, Item.Rarity.UNCOMMON, "aoasteelplatechest", "aoasteelplatearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, -0.03F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoasteelplateboots", new BootsArmorItem(10, 1000, Item.Rarity.UNCOMMON, "aoasteelplateboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, -0.01F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoavoltaichelmet", new SetHelmetArmorItem(10, 1000, Item.Rarity.UNCOMMON, "aoavoltaichelmet", "aoavoltaicchest", "aoavoltaicboots", "aoavoltaicsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.025F), new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.SUMMON_CRIT_CHANCE, 0.03F)});
            }
        }, 110.0F, true);
        registerItem("aoavoltaicchest", new ChestArmorItem(12, 1000, Item.Rarity.UNCOMMON, "aoavoltaicchest", "aoavoltaicarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.05F), new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.SUMMON_ATTACK_SPEED, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoavoltaicboots", new BootsArmorItem(10, 1000, Item.Rarity.UNCOMMON, "aoavoltaicboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.025F), new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoaflorititehelmet", new SetHelmetArmorItem(12, 1000, Item.Rarity.UNCOMMON, "aoaflorititehelmet", "aoaflorititechestplate", "aoaflorititeboots", "aoaflorititesetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_CRIT_CHANCE, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoaflorititechestplate", new ChestArmorItem(14, 1000, Item.Rarity.UNCOMMON, "aoaflorititechest", "aoaflorititearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_CRIT_CHANCE, 0.025F), new ModifierValue(BuffModifiers.SPEED, 0.15F), new ModifierValue(BuffModifiers.RANGED_DAMAGE, 0.15F)});
            }
        }, 160.0F, true);
        registerItem("aoaflorititeboots", new BootsArmorItem(14, 1000, Item.Rarity.UNCOMMON, "aoaflorititeboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_CRIT_CHANCE, 0.025F), new ModifierValue(BuffModifiers.SPEED, 0.10F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoaheavyplatemailhelmet", new SetHelmetArmorItem(14, 1000, Item.Rarity.UNCOMMON, "aoaheavyplatemailhelmet", "aoaheavyplatemailchestplate", "aoaheavyplatemailboots", "aoaheavymailsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN, 0.02F), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 1F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoaheavyplatemailchestplate", new ChestArmorItem(19, 1000, Item.Rarity.UNCOMMON, "aoaheavyplatemailchest", "aoaheavyplatemailarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN, 0.025F), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 1F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoaheavyplatemailboots", new BootsArmorItem(18, 1000, Item.Rarity.UNCOMMON, "aoaheavyplatemailboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN, 0.025F), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 1F), new ModifierValue(BuffModifiers.MELEE_CRIT_CHANCE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoacrystalplatehelmet", new SetHelmetArmorItem(11, 1000, Item.Rarity.UNCOMMON, "crystalplatehelmet", "aoacrystalplatechestplate", "aoacrystalplateboots", "aoacrystalsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.MAGIC_CRIT_CHANCE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoacrystalplatechestplate", new ChestArmorItem(14, 1000, Item.Rarity.UNCOMMON, "crystalplatechest", "crystalplatearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.MAGIC_ATTACK_SPEED, 0.10F)});
            }
        }, 160.0F, true);
        registerItem("aoacrystalplateboots", new BootsArmorItem(8, 1000, Item.Rarity.UNCOMMON, "crystalplateboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.10F)});
            }
        }, 80.0F, true);
        registerItem("aoaaetherhelmet", new SetHelmetArmorItem(16, 1000, Item.Rarity.UNCOMMON, "aoaaetherhelmet", "aoaaetherchestplate", "aoaaetherboots", "aoaaethersetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoaaetherchestplate", new ChestArmorItem(18, 1000, Item.Rarity.UNCOMMON, "aoaaetherchestplate", "aoaaetherarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.5F), new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoaaetherboots", new BootsArmorItem(18, 1000, Item.Rarity.UNCOMMON, "aoaaetherboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.10F)});
            }
        }, 80.0F, true);
        registerItem("aoaaetherknighthelmet", new SetHelmetArmorItem(28, 1000, Item.Rarity.UNCOMMON, "aoaaetherknighthelmet", "aoaaetherknightchestplate", "aoaaetherknightboots", "aoacrystalsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.5F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.MELEE_CRIT_CHANCE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoaaetherknightchestplate", new ChestArmorItem(28, 1000, Item.Rarity.UNCOMMON, "aoaaetherknightchest", "aoaaetherknightarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.01F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.1F), new ModifierValue(BuffModifiers.MAGIC_ATTACK_SPEED, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoaaetherknightboots", new BootsArmorItem(28, 1000, Item.Rarity.UNCOMMON, "aoaaetherknightboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.01F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.18F)});
            }
        }, 80.0F, true);
        registerItem("aoapenumbrahat", new SetHelmetArmorItem(16, 1000, Item.Rarity.UNCOMMON, "aoapenumbrahat", "aoapenumbracoat", "aoapenumbraboots", "aoapenumbrahatsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.10F), new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 100), new ModifierValue(BuffModifiers.MAGIC_CRIT_CHANCE, 0.15F)});
            }
        }, 110.0F, true);
        registerItem("aoapenumbrahood", new SetHelmetArmorItem(16, 1000, Item.Rarity.UNCOMMON, "aoapenumbrahood", "aoapenumbracoat", "aoapenumbraboots", "aoapenumbrahoodsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.10F), new ModifierValue(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue(BuffModifiers.SUMMONS_TARGET_RANGE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoapenumbracoat", new ChestArmorItem(18, 1000, Item.Rarity.UNCOMMON, "aoapenumbracoat", "aoapenumbraarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.MAX_HEALTH, -0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoapenumbraboots", new BootsArmorItem(16, 1000, Item.Rarity.UNCOMMON, "aoapenumbraboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(BuffModifiers.MAX_HEALTH, -0.05F), new ModifierValue(BuffModifiers.SPEED, 0.20F)});
            }
        }, 80.0F, true);
        registerItem("aoaadvancedhelmet", new SetHelmetArmorItem(24, 1000, Item.Rarity.UNCOMMON, "aoaadvancedhelmet", "aoaadvancedchestplate", "aoaadvancedboots", "aoaadvancedsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_DAMAGE, 0.10F), new ModifierValue(BuffModifiers.RANGED_CRIT_DAMAGE, 0.20F), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.10F)});
            }
        }, 110.0F, true);
        registerItem("aoaadvancedchestplate", new ChestArmorItem(24, 1000, Item.Rarity.UNCOMMON, "aoaadvancedchestplate", "aoaadvancedarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_DAMAGE, 0.15F), new ModifierValue(BuffModifiers.MAX_HEALTH, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoaadvancedboots", new BootsArmorItem(24, 1000, Item.Rarity.UNCOMMON, "aoaadvancedboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_CRIT_CHANCE, 0.05F), new ModifierValue(BuffModifiers.MAX_HEALTH, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.20F)});
            }
        }, 80.0F, true);
        registerItem("aoaabysshelmet", new SetHelmetArmorItem(16, 1000, Item.Rarity.UNCOMMON, "aoaabysshelmet", "aoaabysschestplate", "aoaabyssboots", "aoaabysssetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 0.30F)});
            }
        }, 110.0F, true);
        registerItem("aoaabysschestplate", new ChestArmorItem(22, 1000, "aoaabysschestplate", "aoaabyssarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.SUMMONS_TARGET_RANGE, 0.15F)});
            }
        }, 160.0F, true);
        registerItem("aoaabyssboots", new BootsArmorItem(20, 1000, Item.Rarity.UNCOMMON, "aoaabyssboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.SPEED, 0.15F)});
            }
        }, 80.0F, true);
        registerItem("aoashadowstriderhelmet", new SetHelmetArmorItem(24, 1000, Item.Rarity.RARE, "aoashadowstriderhelmet", "aoashadowstriderchestplate", "aoashadowstriderboots", "aoashadowstridersetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.10F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.10F)});
            }
        }, 110.0F, true);
        registerItem("aoashadowstriderchestplate", new ChestArmorItem(26, 1000, Item.Rarity.RARE, "aoashadowstriderchestplate", "aoashadowstriderarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.MELEE_CRIT_CHANCE, 0.15F)});
            }
        }, 160.0F, true);
        registerItem("aoashadowstriderboots", new BootsArmorItem(28, 1000, Item.Rarity.RARE, "aoashadowstriderboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.ARMOR_PEN, 0.20F)});
            }
        }, 80.0F, true);
        registerItem("aoadepthstriderhelmet", new SetHelmetArmorItem(26, 1000, Item.Rarity.RARE, "aoadepthstriderhelmet", "aoadepthstriderchestplate", "aoadepthstriderboots", "aoadepthstridersetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 0.30F)});
            }
        }, 110.0F, true);
        registerItem("aoadepthstriderplatehelmet", new SetHelmetArmorItem(22, 1000, Item.Rarity.RARE, "aoadepthstriderplatehelmet", "aoadepthstriderchestplate", "aoadepthstriderboots", "aoadepthstridersetbonusplate") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{ new ModifierValue(BuffModifiers.MANA_REGEN, 0.30F), new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.20F)});
            }
        }, 110.0F, true);
        registerItem("aoadepthstriderchestplate", new ChestArmorItem(28, 1000, Item.Rarity.RARE, "aoadepthstriderchestplate", "aoadepthstriderarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoadepthstriderboots", new BootsArmorItem(20, 1000, Item.Rarity.RARE, "aoadepthstriderboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue(BuffModifiers.SPEED, 0.15F)});
            }
        }, 80.0F, true);
        registerItem("aoaunfinishedhelmet", new SetHelmetArmorItem(20, 1000, Item.Rarity.UNCOMMON, "aoaunfinishedhelmet", "aoaunfinishedchestplate", "aoaunfinishedboots", "aoaunfinishedsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 0.30F)});
            }
        }, 110.0F, true);
        registerItem("aoaunfinishedchestplate", new ChestArmorItem(22, 1000, "aoaunfinishedchestplate", "aoaunfinishedarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue(BuffModifiers.SUMMONS_TARGET_RANGE, 0.15F)});
            }
        }, 160.0F, true);
        registerItem("aoaunfinishedboots", new BootsArmorItem(20, 1000, Item.Rarity.UNCOMMON, "aoaunfinishedboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue(BuffModifiers.SPEED, 0.15F)});
            }
        }, 80.0F, true);
        registerItem("aoaantplatescalehelmet", new SetHelmetArmorItem(8, 1000, Item.Rarity.RARE, "aoaantplatescalehelmet", "aoaantplatescalechestplate", "aoaantplatescaleboots", "aoaantplatescalesetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.RANGED_DAMAGE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoaantplatescalechestplate", new ChestArmorItem(12, 1000, Item.Rarity.RARE, "aoaantplatescalechestplate", "aoaantplatescalearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.RANGED_CRIT_DAMAGE, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoaantplatescaleboots", new BootsArmorItem(9, 1000, Item.Rarity.RARE, "aoaantplatescaleboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.RANGED_CRIT_CHANCE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoaalloyedhelmet", new SetHelmetArmorItem(12, 1000, Item.Rarity.RARE, "aoaalloyedhelmet", "aoaalloyedchestplate", "aoaalloyedboots", "aoaalloyedsetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.05F)});
            }
        }, 110.0F, true);
        registerItem("aoaalloyedchestplate", new ChestArmorItem(15, 1000, Item.Rarity.RARE, "aoaalloyedchestplate", "aoaalloyedarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.CRIT_DAMAGE, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoaalloyedboots", new BootsArmorItem(14, 1000, Item.Rarity.RARE, "aoaalloyedboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoahoplitehelmet", new SetHelmetArmorItem(16, 1000, Item.Rarity.RARE, "aoahoplitehelmet", "aoahoplitechestplate", "aoahopliteboots", "aoahoplitesetbonus") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.15F)});
            }
        }, 110.0F, true);
        registerItem("aoahoplitechestplate", new ChestArmorItem(18, 1000, Item.Rarity.RARE, "aoahoplitechestplate", "aoahoplitearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.10F), new ModifierValue(BuffModifiers.CRIT_DAMAGE, 0.15F)});
            }
        }, 160.0F, true);
        registerItem("aoahopliteboots", new BootsArmorItem(16, 1000, Item.Rarity.RARE, "aoahopliteboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoadarkmagehood", new SetHelmetArmorItem(8, 1000, Item.Rarity.RARE, "aoadarkmagehood", "aoadarkmagerobe", "aoadarkmageboots", "aoadarkmagesetbonus") {
        public GameTexture lightTexture;

        public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
            return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.05F)});
        }

        protected void loadArmorTexture() {
            super.loadArmorTexture();
            this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
        }

        public DrawOptions getArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, GameTexture mask) {
            DrawOptionsList options = new DrawOptionsList();
            options.add(super.getArmorDrawOptions(item, player, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask));
            Color col = this.getDrawColor(item, player);
            options.add(this.lightTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light.minLevelCopy(150.0F)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addShaderTextureFit(mask, 1).pos(drawX, drawY));
            return options;
        }
    }, 200.0F, true);
        registerItem("aoadarkmagerobe", new ChestArmorItem(9, 1000, Item.Rarity.RARE, "aoadarkmagerobe", "aoadarkmagearms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.MAGIC_CRIT_DAMAGE, 0.05F)});
            }
        }, 160.0F, true);
        registerItem("aoadarkmageboots", new BootsArmorItem(8, 1000, Item.Rarity.RARE, "aoadarkmageboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.MAGIC_CRIT_CHANCE, 0.05F)});
            }
        }, 80.0F, true);
        registerItem("aoaexohelmet", new SetHelmetArmorItem(25, 1000, Item.Rarity.LEGENDARY, "aoaexohelmet", "aoaexochestplate", "aoaexoboots", "aoaexosetbonus") {
            public GameTexture lightTexture;

            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.COMBAT_REGEN_FLAT, 0.30F)});
            }
            protected void loadArmorTexture() {
                super.loadArmorTexture();
                this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
            }

            public DrawOptions getArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, GameTexture mask) {
                DrawOptionsList options = new DrawOptionsList();
                options.add(super.getArmorDrawOptions(item, player, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask));
                Color col = this.getDrawColor(item, player);
                options.add(this.lightTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light.minLevelCopy(150.0F)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addShaderTextureFit(mask, 1).pos(drawX, drawY));
                return options;
            }
        }, 200.0F, true);
        registerItem("aoaexochestplate", new ChestArmorItem(35, 1000, Item.Rarity.LEGENDARY,"aoaexochestplate", "aoaexoarms") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 100), new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.20F)});
            }
        }, 160.0F, true);
        registerItem("aoaexoboots", new BootsArmorItem(25, 1000, Item.Rarity.LEGENDARY, "aoaexoboots") {
            public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
                return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.SPEED, 0.35F)});
            }
        }, 80.0F, true);

        registerItem("aoatemperedsteelbarmaterialitem", new AoATemperedSteelBarMaterialItem(), 10, true);
        registerItem("aoafirelordspawnitem", new AoAFireLordSpawnItem(), 50.0F, true);
        registerItem("aoaserpentspawnitem", new AoASerpentSpawnitem(), 50.0F, true);
        registerItem("aoachaoticspawnitem", new AoAChaoticSpawnItem(), 50.0F, true);
        registerItem("aoaphosphorusmaterialitem", new AoAPhosphorusMaterialItem(), 10, true);
        registerItem("aoasawdust", new AoASawDust(), 10, true);
        registerItem("aoasteelspear", new CustomSpearToolItem(Item.Rarity.UNCOMMON, 400, 75, 140, 25, 1100), 200.0F, true);
        registerItem("aoasyrillitespear", new CustomSpearToolItem(Item.Rarity.UNCOMMON, 450, 110, 140, 25, 1100), 200.0F, true);
        registerItem("aoatemperedsteelspear", new CustomSpearToolItem(Item.Rarity.UNCOMMON, 450, 178, 140, 25, 1100), 200.0F, true);
        registerItem("aoaflorititepike", new CustomSpearToolItem(Item.Rarity.UNCOMMON, 450, 78, 140, 25, 1100), 200.0F, true);
        registerItem("aoasteelaxe", new CustomAxeToolItem(400, 210, 5, 30, 70, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
        registerItem("aoaflorititeaxe", new CustomAxeToolItem(400, 250, 6, 35, 70, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
        registerItem("aoasyrilliteaxe", new CustomAxeToolItem(400, 300, 7, 45, 70, 50, 1000, Item.Rarity.RARE), 160.0F, true);
        registerItem("aoatemperedsteelaxe", new CustomAxeToolItem(400, 350, 8, 50, 80, 50, 1000, Item.Rarity.RARE), 160.0F, true);
        registerItem("aoaadvancedaxe", new CustomAxeToolItem(400, 450, 9, 70, 90, 50, 1000, Item.Rarity.EPIC), 160.0F, true);
        registerItem("aoaadvancedpickaxe", new CustomPickaxeToolItem(500, 400, 9, 80, 80, 50, 100, Item.Rarity.EPIC), 8.0F, true);
        registerItem("aoatemperedsteelpickaxe", new CustomPickaxeToolItem(500, 300, 8, 40, 50, 50, 100, Item.Rarity.RARE), 8.0F, true);
        registerItem("aoasyrillitepickaxe", new CustomPickaxeToolItem(500, 250, 7, 35, 50, 50, 100, Item.Rarity.RARE), 8.0F, true);
        registerItem("aoaflorititepickaxe", new CustomPickaxeToolItem(500, 195, 6, 30, 50, 50, 100, Item.Rarity.UNCOMMON), 8.0F, true);
        registerItem("aoasteelpickaxe", new CustomPickaxeToolItem(500, 195, 6, 28, 50, 50, 100, Item.Rarity.UNCOMMON), 8.0F, true);
        registerItem("aoasteelbarmaterialitem", new AoASteelBarMaterialItem(), 10, true);
        registerItem("aoacaeruleusbar", new AoACaeruleusBar(), 10, true);
        registerItem("aoacrystaldust", new AoACrystalDust(), 10, true);
        registerItem("aoavoltaicdust", new AoAVoltaicDust(), 10, true);
        registerItem("aoaglasspanematerialitem", new AoAGlassPane(), 10, true);
        registerItem("aoavoltaicglasspanematerialitem", new AoAVoltaicGlassPane(), 10, true);
        registerItem("aoasilicate", new AoASilicate(), 10, true);
        registerItem("aoaignuspowdermaterialitem", new AoAIgnusPowderMaterialItem(), 10, true);
        registerItem("aoacrystalbarmaterialitem", new AoACrystalBarMaterialItem(), 10, true);
        registerItem("aoasyrillitebarmaterialitem", new AoASyrilliteBarMaterialItem(), 10, true);
        registerItem("aoaflorititebarmaterialitem", new AoAFlorititeBarMaterialItem(), 10, true);
        registerItem("aoarootresinmaterialitem", new AoARootResinMaterialItem(), 10, true);
        registerItem("aoacrystalshardmaterialitem", new AoACrystalShardMaterialItem(), 10, true);
        registerItem("aoamagicalcatalystmaterialitem", new AoAMagicalCatalystMaterialItem(), 10, true);
        registerItem("aoafloralrockmaterial", new AoAFloralrockMaterial(), 65.0F, true);
        registerItem("aoaoblivionstonematerial", new AoAOblivionStoneMaterial(), 65.0F, true);
        registerItem("aoafrostbornrockmaterial", new AoAFrostbornRockMaterial(), 65.0F, true);
        registerItem("aoashinyrockmaterial", new AoAShinyRockMaterial(), 65.0F, true);
        registerItem("aoasearcherseye", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "aoasearcherseye", 600), 400.0F, true);
        registerItem("aoavalkiryieprothesis", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "aoavalkiryieprothesisbuff", 600), 400.0F, true);
        registerItem("aoaserpentscaleshield", new ShieldTrinketItem(Item.Rarity.UNCOMMON, 5, 0.25F, 7000, 0.19F, 50, 200.0F, 800), 250.0F, true);
        registerItem("aoaserpenthunter", new AoASerpentHunter(), 65.0F, true);
        registerItem("aoarustyknife", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "aoarustyknifebuff", 600), 400.0F, true);
        registerItem("aoawarriorsmedallion", new SimpleTrinketItem(Item.Rarity.RARE, "aoawarriorsmedallionbuff", 200), 50.0F, true);
        registerItem("aoaflamingopalnecklace", new SimpleTrinketItem(Item.Rarity.RARE, "aoaflamingopalnecklacebuff", 200), 50.0F, true);
        registerItem("aoasunblessedamulett", new SimpleTrinketItem(Item.Rarity.RARE, "aoasunblessedamulettbuff", 200), 50.0F, true);
        registerItem("aoaserpenttoothnecklace", new SimpleTrinketItem(Item.Rarity.COMMON, "aoaserpenttoothnecklacebuff", 200), 50.0F, true);
        registerItem("aoaspiritbottletrinket", new SimpleTrinketItem(Item.Rarity.COMMON, "aoaspiritbottletrinketbuff", 200), 50.0F, true);
        registerItem("aoahauntedremains", new AoAHauntedRemains(), 65.0F, true);
        registerItem("aoafrostbornspear", new AoAFrostbornSpearToolItem(), 650.0F, true);
        registerItem("aoafrostlichspear", new AoAFrostLichSpearToolItem(), 1250.0F, true);
        registerItem("aoamagentamossseeditem", new GrassSeedItem("aoamagentamosstile"), 2.0F, true);
        registerItem("aoagoldenmossseeditem", new GrassSeedItem("aoagoldenmosstile"), 2.0F, true);
        registerItem("aoacyanmossseeditem", new GrassSeedItem("aoacyanmosstile"), 2.0F, true);
        registerItem("aoalightbringer", new AoALightBringer(), 90, true);
        registerItem("aoatungstenrevolver", new AoATungstenRevolver(), 90, true);
        registerItem("aoasimplerevolver", new AoASimpleRevolver(), 30, true);
        registerItem("aoabowrifle", new CustomBowProjectileToolItem(Item.Rarity.RARE, 450, 45, 1000, 240, 1400, 12, 12), 800.0F, true);
        registerItem("aoatungstenbowrifle", new CustomBowProjectileToolItem(Item.Rarity.RARE, 450, 70, 1000, 240, 1400, 12, 12), 800.0F, true);
        registerItem("aoaarcanewand", new AoAArcaneWand(), 30, true);
        registerItem("aoalongsword", new CustomSwordToolItem(300, 62, 95, 105, 450), 50.0F, true);
        registerItem("aoabroadsword", new CustomSwordToolItem(300, 82, 75, 105, 450), 50.0F, true);
        registerItem("aoaalloysword", new CustomSwordToolItem(200, 72, 65, 75, 450), 50.0F, true);
        registerItem("aoahoplitesspear", new AoAHoplitesSpear(), 30, true);
        registerItem("aoaantplatescale", new AoAAntPlateScale(), 30, true);
        registerItem("aoadeadlandrockmaterial", new AoADeadlandRockMaterial(), 30, true);
        registerItem("aoaflorititeore", new AoAFlorititeOre(), 30, true);
        registerItem("aoasyrilliteore", new AoASyrilliteOre(), 80, true);
        registerItem("aoademonsword", new AoADemonSword(), 250000, true);
        registerItem("aoabasaltrockmaterial", new AoABasaltRockMaterial(), 50, true);
        registerItem("aoacinderlog", (new MatItem(25000, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
        registerItem("aoarawignusshard", new AoARawIgnusShardMaterialItem(), 50, true);
        registerItem("aoamineralchunk", new AoAMineralChunkMaterialItem(), 50, true);
        registerItem("aoaignusblade", new AoAIgnusBlade(), 5000, true);
        registerItem("aoaassaultrifle", new AoAAssaultRifle(), 50, true);
        registerItem("aoaalpinegrassseed", new GrassSeedItem("aoaalpinegrasstile"), 2.0F, true);
        registerItem("aoaashengrassseed", new GrassSeedItem("aoaashengrasstile"), 2.0F, true);
        registerItem("aoavoyager", new SimpleTrinketItem(Item.Rarity.LEGENDARY, "aoavoyagerbuff", 200), 50.0F, true);
        registerItem("aoaaurumarborisleaf", new AoAAurumArborisLeaf(), 50, true);
        registerItem("aoaaurumarborislog", new AoAAurumArborisLog(), 50, true);
        ItemRegistry.registerItem("exampleitem", new ExampleMaterialItem(), 10, true);
        ItemRegistry.registerItem("examplesword", new ExampleSwordItem(), 20, true);
        ItemRegistry.registerItem("examplestaff", new ExampleProjectileWeapon(), 30, true);

        // Register our mob
        MobRegistry.registerMob("aoatoxicspider", AoAToxicSpider.class, true);
        MobRegistry.registerMob("aoatoxicdevourer", AoAToxicDevourer.class, true);
        MobRegistry.registerMob("aoaskeletonarcher", AoASkeletonArcher.class, true);
        MobRegistry.registerMob("aoafanaticwizard", AoAFanaticWizardMob.class, true);
        MobRegistry.registerMob("aoafanaticmob", AoAFanaticMob.class, true);
        MobRegistry.registerMob("aoafanaticmagemob", AoAFanaticMageMob.class, true);
        MobRegistry.registerMob("aoamalachitecristalline", AoAMalachiteCristalline.class, true);
        MobRegistry.registerMob("aoahermitcrab", AoAHermitCrab.class, true);
        MobRegistry.registerMob("aoacristalline", AoACristalline.class, true);
        MobRegistry.registerMob("aoaswordgolemmob", AoASwordGolemMob.class, true);
        MobRegistry.registerMob("aoaarmoredswordgolemmob", AoAArmoredSwordGolemMob.class, true);
        MobRegistry.registerMob("aoagolemmob", AoAGolemMob.class, true);
        MobRegistry.registerMob("aoaarmoredgolemmob", AoAArmoredGolemMob.class, true);
        MobRegistry.registerMob("aoachaoticmagus", AoAChaoticMagus.class, true);
        MobRegistry.registerMob("aoalordoffire", AoALordofFire.class, true);
        MobRegistry.registerMob("aoalordofaether", AoALordofAether.class, true);
        MobRegistry.registerMob("aoaprecursorguard", AoAPrecursorGuard.class, true);
        MobRegistry.registerMob("aoaexodiggerhead", AoAExoDiggerHead.class, true);
        MobRegistry.registerMob("aoaexodiggerbody", AoAExoDiggerBody.class, true);
        MobRegistry.registerMob("aoaexodiggertail", AoAExoDiggerTail.class, true);
        MobRegistry.registerMob("aoagiantserpenthead", AoAGiantSerpentHead.class, true);
        MobRegistry.registerMob("aoagiantserpentbody", AoAGiantSerpentBody.class, true);
        MobRegistry.registerMob("aoagiantserpenttail", AoAGiantSerpentTail.class, true);
        MobRegistry.registerMob("aoaundeadarcher", AoAUndeadArcher.class, true);
        MobRegistry.registerMob("aoarestlesssoul", AoARestlessSoulMob.class, true);
        MobRegistry.registerMob("aoaundeadknight", AoAUndeadKnightMob.class, true);
        MobRegistry.registerMob("aoaundeadwarrior", AoAUndeadWarriorMob.class, true);
        MobRegistry.registerMob("aoafrostbornmage", AoAFrostbornMageMob.class, true);
        MobRegistry.registerMob("aoafrostbornchampion", AoAFrostbornChampion.class, true);
        MobRegistry.registerMob("aoawisp", AoAWisp.class, true);
        MobRegistry.registerMob("aoaantspiderlarva", AoAAntSpiderLarva.class, true);
        MobRegistry.registerMob("aoagiantantspidermob", AoAGiantAntSpiderMob.class, true);
        MobRegistry.registerMob("examplemob", ExampleMob.class, true);
        MobRegistry.registerMob("aoaflamewolf", AoAFlameWolf.class, true);
        MobRegistry.registerMob("aoaignuscristalline", AoAIgnusCristalline.class, true);

        // Register our projectile
        ProjectileRegistry.registerProjectile("aoatoxicstingerprojectile", AoAToxicStingerProjectile.class, "aoatoxicstinger", "disruptor");
        ProjectileRegistry.registerProjectile("aoatoxiceggprojectile", AoAToxicEggProjectile.class, "queenspideregg", "queenspideregg_shadow");
        ProjectileRegistry.registerProjectile("aoaflametorrentprojectile", AoAFlameTorrentProjectile.class, "aoaflametorrentprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoaflameprojectile", AoAFlameProjectile.class, "aoaflameprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoamoltenvolleyprojectile", AoAMoltenVolleyProjectile.class, "aoaflameprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoaflamebladeprojectile", AoAFlameBladeProjectile.class, "aoaflamebladeprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoabossattackwaveprojectile", AoABossAttackWaveProjectile.class, "aoadarkflamewaveprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoadarkflamearrowprojectile", AoADarkflameArrowProjectile.class, "aoadarkflameprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoafanaticsvolleyprojectile", AoAFanaticsVolleyProjectile.class, "aoafanaticsvolleyprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoaserpenthunterwave", AoASerpentHunterWave.class, "aoaserpenthunterwave", "disruptor");
        ProjectileRegistry.registerProjectile("aoafrostbornarrowprojectile", AoAFrostbornArrowProjectile.class, "ironarrow", "disruptor");
        ProjectileRegistry.registerProjectile("aoafrostlichspearprojectile2", AoAFrostLichSpearProjectile2.class, "aoafrostbornspearprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoafrostlichspearprojectile1", AoAFrostLichSpearProjectile1.class, "aoafrostbornspearprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoafrostbornspearprojectile", AoAFrostbornSpearProjectile.class, "aoafrostbornspearprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoaarcanebolt", AoAArcaneBolt.class, "disruptor", "disruptor");
        ProjectileRegistry.registerProjectile("aoahoplitesspearprojectile", AoAHoplitesSpearProjectile.class, "aoahoplitesspear", "icejavelin_shadow");
        ProjectileRegistry.registerProjectile("aoademonsrainprojectile", AoADemonsRainProjectile.class, "aoademonsrainprojectile", "nightpiercerarrow_shadow");
        ProjectileRegistry.registerProjectile("aoaignuswave", AoAIgnusWave.class, (String)null, (String)null);
        ProjectileRegistry.registerProjectile("aoachaoticwave", AoAChaoticWave.class, (String)null, (String)null);
        ProjectileRegistry.registerProjectile("aoalightbringerprojectile", AoALightbringerProjectile.class, "aoalightbringerprojectile", "disruptor");
        ProjectileRegistry.registerProjectile("aoalightbringerprojectile2", AoALightbringerProjectile2.class, "aoalightbringerprojectile2", "disruptor");

        // Register our LevelEvents
        LevelEventRegistry.registerEvent("aoatoxicexplosion", AoAToxicExplosion.class);
        LevelEventRegistry.registerEvent("aoafrostbornspearexplosionevent", AoAFrostbornSpearExplosionEvent.class);
        LevelEventRegistry.registerEvent("aoaignusexplosion", AoAIgnusExplosion.class);
        LevelEventRegistry.registerEvent("aoachaoticexplosion", AoAChaoticExplosion.class);
        LevelEventRegistry.registerEvent("aoachaoticbomb", AoAChaoticBombAttackEvent.class);

        // Register our buff
        BuffRegistry.registerBuff("aoaunfinishedsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_REGEN, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.SWIM_SPEED, 0.10F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)}));
        BuffRegistry.registerBuff("aoashadowstridersetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.15F),new ModifierValue(BuffModifiers.COMBAT_REGEN, 0.15F), new ModifierValue(BuffModifiers.SPEED, 0.15F), new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.10F)}));
        BuffRegistry.registerBuff("aoadepthstridersetbonusplate", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 200), new ModifierValue(BuffModifiers.ARMOR_PEN_FLAT, 10), new ModifierValue(BuffModifiers.MAGIC_ATTACK_SPEED, 0.20F)}));
        BuffRegistry.registerBuff("aoadepthstridersetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_REGEN, 0.35F), new ModifierValue(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.30F)}));
        BuffRegistry.registerBuff("aoaadvancedsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.30F), new ModifierValue(BuffModifiers.SPEED, 0.10F), new ModifierValue(BuffModifiers.SLOW, -1.00F)}));
        BuffRegistry.registerBuff("aoaabysssetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMON_CRIT_CHANCE, 0.20F), new ModifierValue(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue(BuffModifiers.SUMMON_CRIT_DAMAGE, 0.20F)}));
        BuffRegistry.registerBuff("aoapenumbrahoodsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.15F), new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.05F), new ModifierValue(BuffModifiers.EMITS_LIGHT, true)}));
        BuffRegistry.registerBuff("aoapenumbrahatsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 100), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 0.50F), new ModifierValue(BuffModifiers.EMITS_LIGHT, true)}));
        BuffRegistry.registerBuff("aoaaethersetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.35F), new ModifierValue(BuffModifiers.REGEN, 0.25F), new ModifierValue(BuffModifiers.REGEN_FLAT, 0.25F)}));
        BuffRegistry.registerBuff("aoaitemmagnetbuff", new SimpleTrinketBuff("aoaitemmagnetbuff", new ModifierValue[]{new ModifierValue(BuffModifiers.ITEM_PICKUP_RANGE, 40.0F)}));
        BuffRegistry.registerBuff("aoavoltaicsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 5), new ModifierValue(BuffModifiers.SUMMONS_TARGET_RANGE, 0.3F), new ModifierValue(BuffModifiers.EMITS_LIGHT, true)}));
        BuffRegistry.registerBuff("aoacrystalsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.MAGIC_CRIT_CHANCE, 0.1F), new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 50)}));
        BuffRegistry.registerBuff("aoamalachitesetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.5F), new ModifierValue(BuffModifiers.ARMOR_FLAT, 20)}));
        BuffRegistry.registerBuff("aoaheavymailsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, -0.2F), new ModifierValue(BuffModifiers.RANGED_DAMAGE, -0.2F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.25F)}));
        BuffRegistry.registerBuff("aoasteelsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.1F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.ARMOR_FLAT, 10)}));
        BuffRegistry.registerBuff("aoaflorititesetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.35F), new ModifierValue(BuffModifiers.REGEN, 0.8F), new ModifierValue(BuffModifiers.RANGED_DAMAGE, 0.08F)}));
        BuffRegistry.registerBuff("aoaenvenomedbuff", new AoAEnvenomedBuff());
        BuffRegistry.registerBuff("aoaimmolationbuff", new AoAImmolationBuff());
        BuffRegistry.registerBuff("aoasearcherseyebuff", new SimpleTrinketBuff(new ModifierValue(BuffModifiers.MINING_SPEED, 2.00F)));
        BuffRegistry.registerBuff("aoavalkiryieprothesisbuff", new AoAValkiryieProthesisBuff());
        BuffRegistry.registerBuff("aoasucessivecombo", new AoASucessiveCombo());
        BuffRegistry.registerBuff("aoasunblessedamulettbuff", new AoASunblessedAmulletBuff());
        BuffRegistry.registerBuff("aoasunblessedsearing", new AoASunblessedSearing());
        BuffRegistry.registerBuff("aoarustyknifebuff", new AoARustyKnifeBuff());
        BuffRegistry.registerBuff("aoarustyknifebleeding", new AoARustyKnifeBleeding());
        BuffRegistry.registerBuff("aoawarriorsmedallionbuff", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN, 0.5F), new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.10F), new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.20F)}));
        BuffRegistry.registerBuff("aoaflamingopalnecklacebuff", new SimpleTrinketBuff("aoaflamingopalnecklacetip",new ModifierValue[]{new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F).max(0.0F),new ModifierValue(BuffModifiers.MAX_HEALTH, 0.15F)}));
        BuffRegistry.registerBuff("aoaexosetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN, 2.0F), new ModifierValue(BuffModifiers.SPEED, 0.20F), new ModifierValue(BuffModifiers.SWIM_SPEED, 2.00F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.20F)}));
        BuffRegistry.registerBuff("aoahoplitesetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.30F), new ModifierValue(BuffModifiers.INCOMING_DAMAGE_MOD, 0.05F)}));
        BuffRegistry.registerBuff("aoaalloyedsetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH, 0.10F), new ModifierValue(BuffModifiers.RESILIENCE_REGEN, 0.20F)}));
        BuffRegistry.registerBuff("aoaantplatescalesetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_CRIT_CHANCE, 0.05F), new ModifierValue(BuffModifiers.RANGED_CRIT_DAMAGE, 0.20F), new ModifierValue(BuffModifiers.POISON_DAMAGE, 0.50F)}));
        BuffRegistry.registerBuff("aoadarkmagesetbonus", new SimpleSetBonusBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MANA_REGEN, 0.15F), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 0.20F), new ModifierValue(BuffModifiers.MANA_USAGE, -0.10F)}));
        BuffRegistry.registerBuff("aoaignuspower", new ShownItemCooldownBuff(1, true, "items/aoaignusblade"));
        BuffRegistry.registerBuff("aoavoyagerbuff", new SimpleTrinketBuff("aoavoyagertip", new ModifierValue[]{new ModifierValue(BuffModifiers.BIOME_VIEW_DISTANCE, 20),new ModifierValue(BuffModifiers.TRAVEL_DISTANCE, 20)}));
        BuffRegistry.registerBuff("aoaspiritbottletrinketbuff", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1),new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.10F)}));
        BuffRegistry.registerBuff("aoaserpenttoothnecklacebuff", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ARMOR_PEN_FLAT, 10),new ModifierValue(BuffModifiers.ARMOR_PEN, 0.05F)}));

        // Register our Levels
        LevelRegistry.registerLevel("aoabanefulmarshsurface", AoABanefulMarshSurface.class);
        LevelRegistry.registerLevel("aoaoceansurface", AoAOceanSurface.class);
        LevelRegistry.registerLevel("aoacrystalcave", AoACrystalCave.class);
        LevelRegistry.registerLevel("aoacrystaldeepcave", AoACrystalDeepCave.class);
        LevelRegistry.registerLevel("aoacrystalsurface", AoACrystalSurface.class);
        LevelRegistry.registerLevel("aoadeadlandbogsurface", AoADeadlandBogSurface.class);
        LevelRegistry.registerLevel("aoadeadlandbogcave", AoADeadlandBogCave.class);
        LevelRegistry.registerLevel("aoadeadlandbogdeepcave", AoADeadlandBogDeepCave.class);
        LevelRegistry.registerLevel("aoavolcanicsurface", AoAVolcanicSurface.class);
        LevelRegistry.registerLevel("aoavolcaniccave", AoAVolcanicCave.class);
        LevelRegistry.registerLevel("aoavolcanicdeepcave", AoAVolcanicDeepCave.class);
        LevelRegistry.registerLevel("aoafrostburnedsurface", AoAFrostburnedRuinsSurface.class);
        LevelRegistry.registerLevel("aoafrostburnedcave", AoAFrostburnedRuinsCave.class);
        LevelRegistry.registerLevel("aoafrostburneddeepcave", AoAFrostburnedRuinsDeepCave.class);
        LevelRegistry.registerLevel("aoaalpinedeepcave", AoAAlpineDeepCave.class);
        LevelRegistry.registerLevel("aoaalpinecave", AoAAlpineCave.class);
        LevelRegistry.registerLevel("aoaalpinesurface", AoAAlpineSurface.class);
        LevelRegistry.registerLevel("aoaancientforestsurface", AoAAncientForestSurface.class);
        LevelRegistry.registerLevel("aoaancientforestcave", AoAAncientForestCave.class);
        LevelRegistry.registerLevel("aoaancientforestdeepcave", AoAAncientForestDeepCave.class);

        // Register our Gamemusic
        MusicRegistry.registerMusic("heathazard", "music/heathazard", new LocalMessage("sound/music", "heathazard"), new StaticMessage("Heathazard - Xavius"), new Color(255, 0, 0), new Color(255, 107, 0));
        MusicRegistry.registerMusic("thehive", "music/thehive", new LocalMessage("sound/music", "thehive"), new StaticMessage("The Hive - Xavius"), new Color(173, 173, 139), new Color(143, 67, 67));
        MusicRegistry.registerMusic("asimplelife", "music/asimplelife", new LocalMessage("sound/music", "asimplelife"), new StaticMessage("A simple Life - Xavius"), new Color(185, 255, 145), new Color(47, 108, 30));
        MusicRegistry.registerMusic("earnedrest", "music/earnedrest", new LocalMessage("sound/music", "earnedrest"), new StaticMessage("Earned rest - Xavius"), new Color(90, 126, 73), new Color(6, 35, 4));
        MusicRegistry.registerMusic("disruption", "music/disruption", new LocalMessage("sound/music", "disruption"), new StaticMessage("Disruption - Xavius"), new Color(255, 203, 0), new Color(0, 0, 0));
        MusicRegistry.registerMusic("chaos", "music/chaos", new LocalMessage("sound/music", "chaotic"), new StaticMessage("Chaos - Xavius"), new Color(146, 197, 115), new Color(57, 66, 51));
        MusicRegistry.registerMusic("aether", "music/aether", new LocalMessage("sound/music", "aether"), new StaticMessage("Aetheric - Xavius"), new Color(255, 218, 29), new Color(166, 134, 0));
        MusicRegistry.registerMusic("aetheralt", "music/aetheralt", new LocalMessage("sound/music", "aether"), new StaticMessage("Aetheric Alt - Xavius"), new Color(243, 229, 143), new Color(234, 206, 97));
        MusicRegistry.registerMusic("catacomb", "music/catacomb", new LocalMessage("sound/music", "catacomb"), new StaticMessage("Catacomb - Xavius"), new Color(84, 103, 80), new Color(52, 100, 41));
        MusicRegistry.registerMusic("precursor", "music/precursor", new LocalMessage("sound/music", "precursor"), new StaticMessage("Precursor - Xavius"), new Color(54, 53, 47), new Color(234, 206, 97));
        MusicRegistry.registerMusic("stalwart", "music/stalwart", new LocalMessage("sound/music", "stalwart"), new StaticMessage("Stalwart - Xavius"), new Color(183, 83, 83), new Color(107, 18, 33, 255));
        MusicRegistry.registerMusic("bossbash", "music/bossbash", new LocalMessage("sound/music", "bossbash"), new StaticMessage("Bossbash - Xavius"), new Color(103, 88, 88), new Color(107, 18, 33, 255));
        MusicRegistry.registerMusic("arena", "music/arena", new LocalMessage("sound/music", "arena"), new StaticMessage("Arena - Xavius"), new Color(128, 227, 94), new Color(157, 122, 128, 255));
        MusicRegistry.registerMusic("cascade", "music/cascade", new LocalMessage("sound/music", "cascade"), new StaticMessage("Cascade - Xavius"), new Color(19, 58, 52), new Color(14, 45, 54, 255));
        MusicRegistry.registerMusic("crypt", "music/crypt", new LocalMessage("sound/music", "crypt"), new StaticMessage("Crypt - Xavius"), new Color(55, 140, 127), new Color(0, 0, 0, 255));
        MusicRegistry.registerMusic("battleready", "music/battleready", new LocalMessage("sound/music", "battleready"), new StaticMessage("Battle Ready - Xavius"), new Color(168, 125, 62), new Color(84, 77, 14, 255));
        MusicRegistry.registerMusic("silentcold", "music/silentcold", new LocalMessage("sound/music", "silentcold"), new StaticMessage("Silent Cold - Xavius"), new Color(68, 77, 75), new Color(63, 147, 169, 255));
        MusicRegistry.registerMusic("simplebeat", "music/simplebeat", new LocalMessage("sound/music", "simplebeat"), new StaticMessage("Simple Beat - Xavius"), new Color(74, 180, 149), new Color(52, 129, 37, 255));
        MusicRegistry.registerMusic("frozenwarfield", "music/frozenwarfield", new LocalMessage("sound/music", "frozenwarfield"), new StaticMessage("Frozen Warfield - Xavius"), new Color(164, 227, 210), new Color(107, 178, 164, 255));
        MusicRegistry.registerMusic("clockwork", "music/clockwork", new LocalMessage("sound/music", "clockwork"), new StaticMessage("Clockwork - Xavius"), new Color(180, 159, 74), new Color(129, 75, 37, 255));
        MusicRegistry.registerMusic("fathomless", "music/fathomless", new LocalMessage("sound/music", "fathomless"), new StaticMessage("Fathomless - Xavius"), new Color(255, 0, 0), new Color(66, 66, 66, 255));
    }

    public void initResources() {
        // Sometimes your textures will have a black or other outline unintended under rotation or scaling
        // This is caused by alpha blending between transparent pixels and the edge
        // To fix this, run the preAntialiasTextures gradle task
        // It will process your textures and save them again with a fixed alpha edge color

        AoAToxicSpider.texture = GameTexture.fromFile("mobs/aoatoxicspider");
        AoAFanaticMob.texture = GameTexture.fromFile("mobs/aoafanaticmob");
        AoAFanaticMageMob.texture = GameTexture.fromFile("mobs/aoafanaticmagemob");
        AoAFanaticWizardMob.texture = GameTexture.fromFile("mobs/aoafanaticwizardmob");
        AoAMalachiteCristalline.texture = GameTexture.fromFile("mobs/aoamalachitecristalline");
        AoAHermitCrab.texture = GameTexture.fromFile("mobs/aoahermitcrab");
        AoAGolemMob.texture = GameTexture.fromFile("mobs/aoagolemmob");
        AoASwordGolemMob.texture = GameTexture.fromFile("mobs/aoaswordgolemmob");
        AoAArmoredGolemMob.texture = GameTexture.fromFile("mobs/aoaarmoredgolemmob");
        AoAArmoredSwordGolemMob.texture = GameTexture.fromFile("mobs/aoaarmoredswordgolemmob");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoafirelord");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoatoxicdevourerhead");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoatoxicdevourerleg");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoatoxicdevourerbody");
        AoABraindamage.texture = GameTexture.fromFile("mobs/disruptor");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoachaoticbombparticle");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoachaoticmagus");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoaprecursorguard");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoaexodigger");
        AoABraindamage.texture = GameTexture.fromFile("mobs/aoagiantserpentmob");
        AoAGiantAntSpiderMob.texture = GameTexture.fromFile("mobs/aoagiantspidermob");
        ExampleMob.texture = GameTexture.fromFile("mobs/examplemob");
        AoAAntSpiderLarva.texture = GameTexture.fromFile("mobs/aoaantspiderlarva");
        AoAFlameWolf.texture = GameTexture.fromFile("mobs/aoaflamewolf");
        AoAIgnusCristalline.texture = GameTexture.fromFile("mobs/aoaignuscrystalline");
        AoACristalline.texture = GameTexture.fromFile("mobs/aoacristalline");
        AoAWisp.texture = GameTexture.fromFile("mobs/disruptor");
        AoAUndeadKnightMob.texture = GameTexture.fromFile("mobs/aoaundeadknight");
        AoAUndeadWarriorMob.texture = GameTexture.fromFile("mobs/aoaundeadwarrior");
        AoAFrostbornChampion.texture = GameTexture.fromFile("mobs/aoafrostbornchampion");
        AoAFrostbornMageMob.texture = GameTexture.fromFile("mobs/aoafrostbornmage");
        AoARestlessSoulMob.texture = GameTexture.fromFile("mobs/aoarestlesssoul");
        AoAUndeadArcher.texture = GameTexture.fromFile("mobs/aoaundeadarcher");
    }
    public static Tech AoATIER1EXTRACTOR;
    public static Tech AOAINVENTORSTABLE;
    public static Tech AOAINVENTORSTABLETIER2;
    public static Tech AOAINVENTORSTABLETIER3;
    public static Tech AOAINVENTORSTABLETIER4;
    public static Tech AOAINVENTORSTABLETIER5;
    public static Tech AOAINVENTORSTABLETIER6;
    public static Tech AOASMITHYTIER1;
    public static Tech AOASMITHYTIER2;
    public static Tech AOASMITHYTIER3;
    public static Tech AOASMITHYTIER4;
    public static Tech AOASMITHYTIER5;
    public static Tech AOASIMPLEGRINDER;
    public static Tech AOADEMONICGRINDER;
    public static Tech AOAADVANCEDGRINDER;
    public static Tech AOAINDUSTRIALGRINDER;
    public static Tech AOADECOTABLE;
    public static Tech AOAFUSION;

    public void AoA() {
        AoATIER1EXTRACTOR = registerTech("aoatier1extractor");
        AOAFUSION = registerTech("aoafusion");
        AOADECOTABLE = registerTech("aoadecorationtable");
        AOASIMPLEGRINDER = registerTech("aoasimplegrinder");
        AOADEMONICGRINDER = registerTech("aoademonicgrinder");
        AOAADVANCEDGRINDER = registerTech("aoaadvancedgrinder");
        AOAINDUSTRIALGRINDER = registerTech("aoaindustrialgrinder");
        AOAINVENTORSTABLE = registerTech("aoainventorstable");
        AOAINVENTORSTABLETIER2 = registerTech("aoainventorstabletier2");
        AOAINVENTORSTABLETIER3 = registerTech("aoainventorstabletier3");
        AOAINVENTORSTABLETIER4 = registerTech("aoainventorstabletier4");
        AOAINVENTORSTABLETIER5 = registerTech("aoainventorstabletier5");
        AOAINVENTORSTABLETIER6 = registerTech("aoainventorstabletier6");
        AOASMITHYTIER1 = registerTech("aoasmithytier1");
        AOASMITHYTIER2 = registerTech("aoasmithytier2");
        AOASMITHYTIER3 = registerTech("aoasmithytier3");
        AOASMITHYTIER4 = registerTech("aoasmithytier4");
        AOASMITHYTIER5 = registerTech("aoasmithytier5");
    }

    public void postInit() {
        // Add recipes
        // Example item recipe, crafted in inventory for 2 iron bars
        Recipes.registerModRecipe(new Recipe(
                "aoafirelordspawnitem",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoachaoticspawnitem",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaserpentspawnitem",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelpickaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititepickaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoaflorititebarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititeaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoaflorititebarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasyrillitepickaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasyrillitebarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasyrilliteaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasyrillitebarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoatemperedsteelpickaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoatemperedsteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoatemperedsteelaxe",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoatemperedsteelbarmaterialitem", 8),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititechestplate",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoaflorititebarmaterialitem", 5),
                        new Ingredient("leather", 10)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititehelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoaflorititebarmaterialitem", 5),
                        new Ingredient("leather", 10)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititeboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoaflorititebarmaterialitem", 5),
                        new Ingredient("leather", 10)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoavoltaichelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 15),
                        new Ingredient("aoavoltaicglasspanematerialitem", 6),
                        new Ingredient("leather", 10)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoavoltaicchest",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 12),
                        new Ingredient("aoavoltaicglasspanematerialitem", 7),
                        new Ingredient("leather", 3)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoavoltaicboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                        new Ingredient("aoavoltaicglasspanematerialitem", 5),
                        new Ingredient("leather", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelplatechest",
                1,
                RecipeTechRegistry.getTech("aoasmithytier4"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 15),
                        new Ingredient("leather", 10)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelplatehelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier4"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 12),
                        new Ingredient("leather", 3)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelplateboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier4"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 8),
                        new Ingredient("leather", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalplatehelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoacrystalbarmaterialitem", 5),
                        new Ingredient("aoasteelbarmaterialitem", 5),
                        new Ingredient("aoacrystalshardmaterialitem", 5),
                        new Ingredient("aoamagicalcatalystmaterialitem", 3)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalplatechestplate",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoacrystalbarmaterialitem", 5),
                        new Ingredient("aoasteelbarmaterialitem", 5),
                        new Ingredient("aoamagicalcatalystmaterialitem", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalplateboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier5"),
                new Ingredient[]{
                        new Ingredient("aoacrystalbarmaterialitem", 5),
                        new Ingredient("aoasteelbarmaterialitem", 5),
                        new Ingredient("aoamagicalcatalystmaterialitem", 2)
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacaeruleusbar",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("aoacrystalbarmaterialitem", 1),
                        new Ingredient("bloodessence", 1),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaglasspanematerialitem",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("aoacrystaldust", 2),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoavoltaicglasspanematerialitem",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("aoaglasspanematerialitem", 2),
                        new Ingredient("aoavoltaicdust", 1)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalbarmaterialitem",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("aoacrystalshardmaterialitem", 5),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelbarmaterialitem",
                2,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                        new Ingredient("aoaignuspowdermaterialitem", 1),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelbarmaterialitem",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("ironore", 2),
                        new Ingredient("aoaignuspowdermaterialitem", 1),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasyrillitebarmaterialitem",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("aoasyrilliteore", 2),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititebarmaterialitem",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("aoaflorititeore", 2),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstable",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("ironbar", 5),
                        new Ingredient("copperbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstabletier2",
                1,
                RecipeTechRegistry.getTech("aoainventorstable"),
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("demonicbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstabletier2",
                1,
                RecipeTechRegistry.getTech("aoainventorstable"),
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("ironbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstabletier3",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier2"),
                new Ingredient[]{
                        new Ingredient("ivybar", 5),
                        new Ingredient("quartz", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstabletier4",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier3"),
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("tungstenbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstabletier5",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("obsidian", 5),
                        new Ingredient("ancientfossilbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoainventorstabletier6",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier5"),
                new Ingredient[]{
                        new Ingredient("aoasteelbarmaterialitem", 5),
                        new Ingredient("aoaglasspanematerialitem", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasmithytier1",
                1,
                RecipeTechRegistry.getTech("aoainventorstable"),
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("ironbar", 5),
                        new Ingredient("goldbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoatier1extractor",
                1,
                RecipeTechRegistry.getTech("aoainventorstable"),
                new Ingredient[]{
                        new Ingredient("anylog", 5),
                        new Ingredient("ironbar", 5),
                        new Ingredient("goldbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoatier2extractor",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier5"),
                new Ingredient[]{
                        new Ingredient("aoatier1extractor", 1),
                        new Ingredient("obsidian", 5),
                        new Ingredient("anystone", 20),
                        new Ingredient("ancientfossilbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasmithytier2",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier2"),
                new Ingredient[]{
                        new Ingredient("anystone", 5),
                        new Ingredient("voidshard", 5),
                        new Ingredient("demonicbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoarefrigerator",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier2"),
                new Ingredient[]{
                        new Ingredient("ironbar", 5),
                        new Ingredient("coolingbox", 1),
                        new Ingredient("demonicbar", 2)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasmithytier3",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier3"),
                new Ingredient[]{
                        new Ingredient("ivybar", 5),
                        new Ingredient("frostshard", 5),
                        new Ingredient("quartz", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasmithytier4",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("anylog", 7),
                        new Ingredient("lifequartz", 5),
                        new Ingredient("tungstenbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasmithytier5",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier5"),
                new Ingredient[]{
                        new Ingredient("obsidian", 5),
                        new Ingredient("anystone", 20),
                        new Ingredient("ancientfossilbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoadeepstoneforge",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("deepstone", 25),
                        new Ingredient("forge", 1),
                        new Ingredient("tungstenbar", 1)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoafallenforge",
                1,
                RecipeTechRegistry.getTech("aoainventorstabletier4"),
                new Ingredient[]{
                        new Ingredient("obsidian", 25),
                        new Ingredient("aoadeepstoneforge", 1),
                        new Ingredient("ancientfossilbar", 2)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoasimplerevolver",
                1,
                RecipeTechRegistry.getTech("aoasmithytier1"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("anylog", 1),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoatungstenrevolver",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("tungstenbar", 5),
                        new Ingredient("anylog", 2),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoatungstenbowrifle",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("aoabowrifle", 1),
                        new Ingredient("ivybar", 4),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoabowrifle",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("demonicbar", 10),
                        new Ingredient("anylog", 1),
                        new Ingredient("goldbar", 4),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaarcanewand",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("blueberry", 10),
                        new Ingredient("anylog", 1),
                        new Ingredient("goldbar", 4),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaantplatescalehelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("aoaantplatescale", 15),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaantplatescalechestplate",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("aoaantplatescale", 20),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaantplatescaleboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("aoaantplatescale", 17),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaalloyedhelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("demonichelmet", 1),
                        new Ingredient("ironhelmet", 1),
                        new Ingredient("goldbar", 6),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaalloyedchestplate",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("demonicchestplate", 1),
                        new Ingredient("ironchestplate", 1),
                        new Ingredient("goldbar", 7),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaalloyedboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("demonicboots", 1),
                        new Ingredient("ironboots", 1),
                        new Ingredient("goldbar", 5),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoahoplitehelmet",
                1,
                RecipeTechRegistry.getTech("aoasmithytier3"),
                new Ingredient[]{
                        new Ingredient("goldhelmet", 1),
                        new Ingredient("quartz", 5),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoahoplitechestplate",
                1,
                RecipeTechRegistry.getTech("aoasmithytier3"),
                new Ingredient[]{
                        new Ingredient("goldchestplate", 1),
                        new Ingredient("quartz", 8),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoahopliteboots",
                1,
                RecipeTechRegistry.getTech("aoasmithytier3"),
                new Ingredient[]{
                        new Ingredient("goldboots", 1),
                        new Ingredient("quartz", 6),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoalongsword",
                1,
                RecipeTechRegistry.getTech("aoasmithytier2"),
                new Ingredient[]{
                        new Ingredient("ironbar", 3),
                        new Ingredient("quartz", 6),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoabroadsword",
                1,
                RecipeTechRegistry.getTech("aoasmithytier3"),
                new Ingredient[]{
                        new Ingredient("ironbar", 8),
                        new Ingredient("frostshard", 2),
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "aoaalloysword",
                1,
                RecipeTechRegistry.getTech("aoasmithytier3"),
                new Ingredient[]{
                        new Ingredient("ironsword", 1),
                        new Ingredient("goldsword", 1),
                        new Ingredient("demonicsword", 1),
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "copperore",
                3,
                RecipeTechRegistry.getTech("aoasimplegrinder"),
                new Ingredient[]{
                        new Ingredient("stone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "ironore",
                2,
                RecipeTechRegistry.getTech("aoasimplegrinder"),
                new Ingredient[]{
                        new Ingredient("stone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "goldore",
                1,
                RecipeTechRegistry.getTech("aoasimplegrinder"),
                new Ingredient[]{
                        new Ingredient("stone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "ivyore",
                1,
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                new Ingredient[]{
                        new Ingredient("swampstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "frostshard",
                1,
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                new Ingredient[]{
                        new Ingredient("snowstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "quartz",
                1,
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                new Ingredient[]{
                        new Ingredient("sandstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "lifequartz",
                1,
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                new Ingredient[]{
                        new Ingredient("deepstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "tungstenore",
                1,
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                new Ingredient[]{
                        new Ingredient("deepstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "glacialore",
                1,
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                new Ingredient[]{
                        new Ingredient("deepsnowstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "myceliumore",
                1,
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                new Ingredient[]{
                        new Ingredient("deepswampstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "ancientfossilore",
                1,
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                new Ingredient[]{
                        new Ingredient("deepsandstone", 5)
                }));
        Recipes.registerModRecipe(new Recipe(
                "obsidian",
                20,
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                new Ingredient[]{
                        new Ingredient("deepsandstone", 5),
                        new Ingredient("lavatile", 1)
                }));
        Recipes.registerModRecipe(new Recipe(
                "demonicbar",
                4,
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                new Ingredient[]{
                        new Ingredient("lavatile", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "voidshard",
                5,
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                new Ingredient[]{
                        new Ingredient("lavatile", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "glacialshard",
                1,
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                new Ingredient[]{
                        new Ingredient("deepsnowstone", 5),
                        new Ingredient("frostshard", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasyrilliteore",
                1,
                RecipeTechRegistry.getTech("aoaindustrialgrinder"),
                new Ingredient[]{
                        new Ingredient("aoadeadlandrockmaterial", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaflorititeore",
                1,
                RecipeTechRegistry.getTech("aoaindustrialgrinder"),
                new Ingredient[]{
                        new Ingredient("aoaoblivionstonematerial", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoarawignusshard",
                10,
                RecipeTechRegistry.getTech("aoaindustrialgrinder"),
                new Ingredient[]{
                        new Ingredient("aoabasaltrockmaterial", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalshardmaterialitem",
                10,
                RecipeTechRegistry.getTech("aoaindustrialgrinder"),
                new Ingredient[]{
                        new Ingredient("aoashinyrockmaterial", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoarootresinmaterialitem",
                10,
                RecipeTechRegistry.getTech("aoaindustrialgrinder"),
                new Ingredient[]{
                        new Ingredient("aoafloralrockmaterial", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoarootresinmaterialitem",
                2,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoarootresin", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalshardmaterialitem",
                8,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoacrystalcluster", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystalshardmaterialitem",
                4,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoacrystalclustersmall", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoarawignusshard",
                8,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoaignuscluster", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "fertilizer",
                20,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("spoiledfood", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "fertilizer",
                10,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoasawdust", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasawdust",
                2,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("anylog", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoarawignusshard",
                4,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoaignusclustersmall", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaignuspowdermaterialitem",
                2,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoarawignusshard", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamagicalcatalystmaterialitem",
                4,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoahauntedremains", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaphosphorusmaterialitem",
                4,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoadeadlandrockmaterial", 10),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoavoltaicdust",
                1,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoaphosphorusmaterialitem", 4),
                        new Ingredient("aoashinyrockmaterial", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacrystaldust",
                4,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("aoacrystalshardmaterialitem", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasilicate",
                1,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("sandtile", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "sandtile",
                1,
                RecipeTechRegistry.getTech("aoatier1extractor"),
                new Ingredient[]{
                        new Ingredient("anystone", 4),
                }));



        Recipes.registerModRecipe(new Recipe(
                "aoadarkstonemodulartable",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoalabmodulartable",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabigstreetlightobject",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoawalllight",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoawalllightlow",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoawillowcrate",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("willowlog", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoapinecrate",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("pinelog", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasprucecrate",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("sprucelog", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaoakcrate",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("oaklog", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaoakbarrelstand",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anylog", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabarrelstackobject",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anylog", 4),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaglassfence",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaglassfencegate",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoachainlinkfence",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoachainlinkgate",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasimpleredironfence",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasimplegreenironfence",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasimpleironfence",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoahedgefence",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anylog", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelfactorypathtile",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoastonebrickslab",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasteelfactorytile",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasmallovergrownstonebricks",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasmallmossystonebricks",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoasmallstonebricks",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaconcretepath",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaancientwall",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaancientwall",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaancientdoor",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoadarkstonedoor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoadarkstonewall",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoadarkstoneglasswall",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoadarkstonewoodwindowwall",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamosswall",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamossdoor",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaancientwall",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaancientdoor",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoapolishedsteelwall",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoapolishedsteeldoor",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltpolishedfloor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaroughtconncectingfloorplate",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoaroughtfloorplate",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltfinetileddarkfloor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltfinetiledfloor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltslabtile",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasalttiledfloor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltbrightslab",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltwall",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoabasaltdoor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoachiseledbasaltwall",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoachiseledbasaltdoor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anystone", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoagrownvinewallobject",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anylog", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoavinewallobject",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("anylog", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamoderndresser",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamodernbookshelf",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamoderncabinet",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamoderndinnertable",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 3),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamodernbathtub",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 5),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoamoderntoilet",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoalabofficechair",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoalabtablechems",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoalabtablefluids",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoalabtable",
                1,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacastlebrickwall",
                20,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("clay", 1),
                }));
        Recipes.registerModRecipe(new Recipe(
                "aoacastlebrickdoor",
                2,
                RecipeTechRegistry.getTech("aoadecorationtable"),
                new Ingredient[]{
                        new Ingredient("clay", 1),
                }));
        // Add out example mob to default cave mobs.
        // Spawn tables use a ticket/weight system. In general, common mobs have about 100 tickets.
        Biome.defaultSurfaceMobs
                .add(100, "aoahermitcrab");
        // Register our server chat command
    }

}
