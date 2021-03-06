package al132.techemistry.blocks.steam_turbine;

import al132.alib.tiles.CustomEnergyStorage;
import al132.alib.tiles.EnergyTile;
import al132.alib.tiles.GuiTile;
import al132.techemistry.Ref;
import al132.techemistry.blocks.BaseTile;
import al132.techemistry.utils.TUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Optional;

public class SteamTurbineTile extends BaseTile implements ITickableTileEntity, GuiTile, EnergyTile {
    public SteamTurbineTile() {
        super(Ref.steamTurbineTile);
    }

    public static final int MAX_ENERGY = 100000;

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new SteamTurbineContainer(id, world, pos, inv, player);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.energy = new EnergyStorage(MAX_ENERGY, MAX_ENERGY, MAX_ENERGY, compound.getInt("energy"));
        markDirtyGUI();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("energy", energy.getEnergyStored());
        return super.write(compound);
    }

    @Override
    public void tick() {
        if (world.isRemote) return;
        //energy.receiveEnergy(1, false);
        distributeEnergy();
        markDirtyGUI();
    }

    private void distributeEnergy() {
        Optional<IEnergyStorage> neighbors = TUtils.getSurroundingEnergyTiles(world, pos).stream()
                .filter(LazyOptional::isPresent)
                .map(x -> x.orElse(null))
                .filter(x -> x.getEnergyStored() < x.getMaxEnergyStored())
                .findFirst();
        neighbors.ifPresent(x -> {
            int transferred = energy.extractEnergy(x.receiveEnergy(100, true), true);
            energy.extractEnergy(transferred, false);
            x.receiveEnergy(transferred, false);
        });
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(MAX_ENERGY);
    }

    @Override
    public IEnergyStorage getEnergy() {
        return this.energy;
    }
}