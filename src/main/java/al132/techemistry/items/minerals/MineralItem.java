package al132.techemistry.items.minerals;

import al132.chemlib.ChemLib;
import al132.chemlib.chemistry.ChemicalStack;
import al132.techemistry.items.BaseItem;
import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class MineralItem extends BaseItem {
    private String formula = "";
    public final List<ChemicalStack> components;

    public MineralItem(String name, List<ChemicalStack> components) {
        super(name, new Item.Properties());
        this.components = components;
    }

    public MineralItem(String name, String formula) {
        this(name, (List<ChemicalStack>) null);
        this.formula = formula;
    }

    public MineralItem(String name, ChemicalStack... components) {
        this(name, Lists.newArrayList(components));
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (!formula.isEmpty()) {
            tooltip.add(new StringTextComponent(formula).mergeStyle(ChemLib.CHEM_TOOLTIP_COLOR));
        } else if (components != null) {
            String line = components.stream().map(ChemicalStack::getAbbreviation).collect(Collectors.joining());
            if (line.charAt(0) == '(') line = line.substring(1, line.length() - 1);
            tooltip.add(new StringTextComponent(line).mergeStyle(ChemLib.CHEM_TOOLTIP_COLOR));//.getAbbreviation()));
        }
    }
}
