package fuzs.tradingpost.block;

import fuzs.tradingpost.TradingPost;
import fuzs.tradingpost.element.TradingPostElement;
import fuzs.tradingpost.entity.merchant.MerchantCollection;
import fuzs.tradingpost.inventory.container.TradingPostContainer;
import fuzs.tradingpost.mixin.accessor.VillagerEntityAccessor;
import fuzs.tradingpost.tileentity.TradingPostTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class TradingPostBlock extends Block {

    private static final VoxelShape LEG1 = Block.box(0.0, 0.0, 0.0, 4.0, 8.0, 4.0);
    private static final VoxelShape LEG2 = Block.box(16.0, 0.0, 0.0, 12.0, 8.0, 4.0);
    private static final VoxelShape LEG3 = Block.box(0.0, 0.0, 16.0, 4.0, 8.0, 12.0);
    private static final VoxelShape LEG4 = Block.box(16.0, 0.0, 16.0, 12.0, 8.0, 12.0);
    private static final VoxelShape TOP = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SHAPE = VoxelShapes.or(TOP, LEG1, LEG2, LEG3, LEG4);

    public static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("container.trading_post");
    private static final TranslationTextComponent NO_MERCHANT_FOUND = new TranslationTextComponent("trading_post.not_found");

    public TradingPostBlock(Properties blockProperties) {

        super(blockProperties);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {

        return true;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {

        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {

        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {

        return new TradingPostTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {

        if (level.isClientSide) {

            return ActionResultType.SUCCESS;
        } else {

            Vector3d blockCenterPos = Vector3d.atCenterOf(pos);
            List<AbstractVillagerEntity> nearbyTraders = level.getEntitiesOfClass(AbstractVillagerEntity.class, new AxisAlignedBB(blockCenterPos.add(-8.0, -5.0, -8.0), blockCenterPos.add(8.0, 5.0, 8.0)), this::canTrade);
            if (!nearbyTraders.isEmpty()) {

                MerchantCollection merchants = new MerchantCollection(player);
                nearbyTraders.forEach(merchant -> {

                    if (merchant instanceof VillagerEntity) {

                        ((VillagerEntityAccessor) merchant).callUpdateSpecialPrices(player);
                    }

                    merchants.addMerchant(merchant.getId(), merchant);
                });

                merchants.setTradingPlayer(player);
                merchants.buildOffers(merchants.getIdToOfferCountMap());
                this.openTradingScreen(player, merchants);
            } else {

                player.displayClientMessage(NO_MERCHANT_FOUND, false);
            }

            return ActionResultType.CONSUME;
        }
    }

    private boolean canTrade(AbstractVillagerEntity villager) {

        return villager.isAlive() && !villager.isTrading() && !villager.isSleeping() && !villager.isBaby() && !villager.getOffers().isEmpty();
    }

    private void openTradingScreen(PlayerEntity player, MerchantCollection merchants) {

        player.openMenu(new SimpleNamedContainerProvider((containerMenuId, playerInventory, playerEntity) -> new TradingPostContainer(containerMenuId, playerInventory, merchants), TradingPostBlock.CONTAINER_TITLE))
                .ifPresent(merchants::sendMerchantData);
    }

    @Override
    public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {

        if (p_180633_5_.hasCustomHoverName()) {

            TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
            if (tileentity instanceof TradingPostTileEntity) {

                ((TradingPostTileEntity) tileentity).setCustomName(p_180633_5_.getHoverName());
            }
        }
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {

        return false;
    }

}
