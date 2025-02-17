package rando.beasts.common.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rando.beasts.common.entity.item.EntityFallingCoconut;
import rando.beasts.common.init.BeastsBlocks;
import rando.beasts.common.init.BeastsItems;
import rando.beasts.common.tileentity.TileEntityCoconut;

@SuppressWarnings("deprecation")
public class BlockCoconut extends BlockContainer {

    private static final AxisAlignedBB AABB =  new AxisAlignedBB(0.1, 0, 0.25, 0.6, 0.2, 0.75);

    public BlockCoconut() {
        super(Material.WOOD);
        String name = "coconut";
        setUnlocalizedName(name);
        setRegistryName(name);
        BeastsBlocks.LIST.add(this);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) this.checkFallable(worldIn, pos);
    }

    private void checkFallable(World worldIn, BlockPos pos) {
        if ((worldIn.isAirBlock(pos.down()) || BlockFalling.canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0) {
            int i = 32;
            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                if (!worldIn.isRemote) {
                    EntityFallingCoconut coconut = new EntityFallingCoconut(worldIn, pos);
                    worldIn.spawnEntity(coconut);
                }
            } else {
                IBlockState state = worldIn.getBlockState(pos);
                worldIn.setBlockToAir(pos);
                BlockPos blockpos;
                for (blockpos = pos.down(); (worldIn.isAirBlock(blockpos) || BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {}
                if (blockpos.getY() > 0) worldIn.setBlockState(blockpos.up(), state);
            }
        }
    }

    public int tickRate(World worldIn) {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(16) == 0) {
            BlockPos blockpos = pos.down();

            if (BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) {
                double d0 = pos.getX() + rand.nextFloat();
                double d1 = pos.getY() - 0.05;
                double d2 = pos.getZ() + rand.nextFloat();
                worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(stateIn));
            }
        }
    }

    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityCoconut();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return BeastsItems.COCONUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Random rand = playerIn.getRNG();
        for(int i = 0; i < 4; ++i) worldIn.spawnParticle(EnumParticleTypes.BLOCK_DUST, hitX + rand.nextDouble(), hitY + rand.nextDouble(), hitZ + rand.nextDouble(), (rand.nextDouble() - 0.5D) * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D, Block.REGISTRY.getIDForObject(Blocks.SAND));
        removedByPlayer(state, worldIn, pos, playerIn, true);
        dropBlockAsItem(worldIn, pos, state, 0);
        return true;
    }

    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
