package fuzs.tradingpost.element;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.extension.ClientExtensibleElement;
import fuzs.tradingpost.TradingPost;
import fuzs.tradingpost.block.TradingPostBlock;
import fuzs.tradingpost.client.element.TradingPostExtension;
import fuzs.tradingpost.inventory.container.TradingPostContainer;
import fuzs.tradingpost.network.message.SBuildOffersMessage;
import fuzs.tradingpost.network.message.SMerchantDataMessage;
import fuzs.tradingpost.network.message.SRemoveMerchantMessage;
import fuzs.tradingpost.tileentity.TradingPostTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ObjectHolder;

public class TradingPostElement extends ClientExtensibleElement<TradingPostExtension> {

    @ObjectHolder(TradingPost.MODID + ":" + "trading_post")
    public static final Block TRADING_POST_BLOCK = null;
    @ObjectHolder(TradingPost.MODID + ":" + "trading_post")
    public static final Item TRADING_POST_ITEM = null;
    @ObjectHolder(TradingPost.MODID + ":" + "trading_post")
    public static final TileEntityType<TradingPostTileEntity> TRADING_POST_TILE_ENTITY = null;
    @ObjectHolder(TradingPost.MODID + ":" + "trading_post")
    public static final ContainerType<TradingPostContainer> TRADING_POST_CONTAINER = null;

    public TradingPostElement() {

        super(element -> new TradingPostExtension((TradingPostElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"A unique utility for the true bargain hunter looking for the best possible deal."};
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void constructCommon() {

        PuzzlesLib.getNetworkHandler().registerMessage(SMerchantDataMessage::new, LogicalSide.CLIENT);
        PuzzlesLib.getNetworkHandler().registerMessage(SRemoveMerchantMessage::new, LogicalSide.CLIENT);
        PuzzlesLib.getNetworkHandler().registerMessage(SBuildOffersMessage::new, LogicalSide.CLIENT);

        PuzzlesLib.getRegistryManager().registerBlockWithItem("trading_post", () -> new TradingPostBlock(AbstractBlock.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)), ItemGroup.TAB_DECORATIONS);
        PuzzlesLib.getRegistryManager().registerTileEntityType("trading_post", () -> TileEntityType.Builder.of(TradingPostTileEntity::new, TRADING_POST_BLOCK).build(null));
        PuzzlesLib.getRegistryManager().registerContainerType("trading_post", () -> new ContainerType<>(TradingPostContainer::new));
    }

    @Override
    public void setupCommon2() {

        PuzzlesLib.getFuelManager().addWoodenBlock(TRADING_POST_BLOCK);
    }

}
