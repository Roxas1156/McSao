package io.github.bfox1.SwordArtOnline.world;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import io.github.bfox1.SwordArtOnline.common.util.Reference;
import io.github.bfox1.SwordArtOnline.init.BlockInit;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Created by Earbuds on 4/13/2016.
 */
public class SAOTeleporter extends Teleporter {
    private final WorldServer worldServerInstance;
    private final Random random;
    private final LongHashMap<PortalPosition> destinationCoordinateCache = new LongHashMap();
    private final List<Long> destinationCoordinateKeys = Lists.<Long>newArrayList();

    public SAOTeleporter(WorldServer world) {
    	super(world);
        this.worldServerInstance = world;
        this.random = new Random(world.getSeed());
    }

    public void placeInPortal(Entity entity, float rotationYaw) {
        if(this.worldServerInstance.provider.getDimensionId() != Reference.saoDimensionId) {
            if(!this.placeInExistingPortal(entity, rotationYaw)) {
                this.makePortal(entity);
                this.placeInExistingPortal(entity, rotationYaw);
            }
        }else {
            int i = MathHelper.floor_double(entity.posX);
            int j = MathHelper.floor_double(entity.posY) - 1;
            int k = MathHelper.floor_double(entity.posZ);

            entity.setLocationAndAngles((double)i, (double)j, (double)k, entity.rotationYaw, 0.0F);
            entity.motionX = entity.motionY = entity.motionZ = 0.0D;
        }
    }
    
    public void removeStalePortalLocations(long worldTime) {
        if(worldTime % 100L == 0L) {
            Iterator<Long> iterator = this.destinationCoordinateKeys.iterator();
            long i = worldTime - 300L;

            while(iterator.hasNext()) {
                Long olong = (Long)iterator.next();
                PortalPosition teleporter$portalposition = (PortalPosition)this.destinationCoordinateCache.getValueByKey(olong.longValue());

                if(teleporter$portalposition == null || teleporter$portalposition.lastUpdateTime < i) {
                    iterator.remove();
                    this.destinationCoordinateCache.remove(olong.longValue());
                }
            }
        }
    }

    public class PortalPosition extends BlockPos {
        public long lastUpdateTime;

        public PortalPosition(BlockPos pos, long lastUpdate) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.lastUpdateTime = lastUpdate;
        }
    }
}