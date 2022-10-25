package de.rytrox.spicy.reflect;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class NBTItemStacksTest {

    private final ItemStack itemStack = new ItemStack(Material.STONE);

    @BeforeAll
    public static void setup() {
        MockBukkit.mock();
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void shouldCatchIllegalAccessExceptionInNMSCopy() {
        try(MockedStatic<MethodUtils> methodUtils = Mockito.mockStatic(MethodUtils.class);
            MockedStatic<Reflections> reflections = Mockito.mockStatic(Reflections.class)) {
            reflections.when(() -> Reflections.getCraftBukkitClass(Mockito.eq("inventory.CraftItemStack")))
                    .thenAnswer((invocationOnMock) -> CraftItemStack.class);

            methodUtils.when(() -> MethodUtils.invokeStaticMethod(Mockito.any(Class.class), Mockito.eq("asNMSCopy"), Mockito.eq(itemStack)))
                    .thenThrow(IllegalAccessException.class);

            net.minecraft.world.item.ItemStack copy = ItemStacks.asNMSCopy(itemStack);
            assertNull(copy);
        }
    }

    @Test
    public void shouldCatchIllegalArgumentExceptionInNMSCopy() {
        try(MockedStatic<MethodUtils> methodUtils = Mockito.mockStatic(MethodUtils.class);
            MockedStatic<Reflections> reflections = Mockito.mockStatic(Reflections.class)) {
            reflections.when(() -> Reflections.getCraftBukkitClass(Mockito.eq("inventory.CraftItemStack")))
                    .thenAnswer((invocationOnMock) -> CraftItemStack.class);

            methodUtils.when(() -> MethodUtils.invokeStaticMethod(Mockito.any(Class.class), Mockito.eq("asNMSCopy"), Mockito.eq(itemStack)))
                    .thenThrow(IllegalArgumentException.class);

            net.minecraft.world.item.ItemStack copy = ItemStacks.asNMSCopy(itemStack);
            assertNull(copy);
        }
    }

    @Test
    public void shouldCatchInvocationTargetExceptionInNMSCopy() {
        try(MockedStatic<MethodUtils> methodUtils = Mockito.mockStatic(MethodUtils.class);
            MockedStatic<Reflections> reflections = Mockito.mockStatic(Reflections.class)) {
            reflections.when(() -> Reflections.getCraftBukkitClass(Mockito.eq("inventory.CraftItemStack")))
                    .thenAnswer((invocationOnMock) -> CraftItemStack.class);

            methodUtils.when(() -> MethodUtils.invokeStaticMethod(Mockito.any(Class.class), Mockito.eq("asNMSCopy"), Mockito.eq(itemStack)))
                    .thenThrow(InvocationTargetException.class);

            net.minecraft.world.item.ItemStack copy = ItemStacks.asNMSCopy(itemStack);
            assertNull(copy);
        }
    }

    @Test
    public void shouldCatchSecurityExceptionInNMSCopy() {
        try(MockedStatic<MethodUtils> methodUtils = Mockito.mockStatic(MethodUtils.class);
            MockedStatic<Reflections> reflections = Mockito.mockStatic(Reflections.class)) {
            reflections.when(() -> Reflections.getCraftBukkitClass(Mockito.eq("inventory.CraftItemStack")))
                    .thenAnswer((invocationOnMock) -> CraftItemStack.class);

            methodUtils.when(() -> MethodUtils.invokeStaticMethod(Mockito.any(Class.class), Mockito.eq("asNMSCopy"), Mockito.eq(itemStack)))
                    .thenThrow(SecurityException.class);

            net.minecraft.world.item.ItemStack copy = ItemStacks.asNMSCopy(itemStack);
            assertNull(copy);
        }
    }

    @Test
    public void shouldCatchNoSuchMethodExceptionInNMSCopy() {
        try(MockedStatic<MethodUtils> methodUtils = Mockito.mockStatic(MethodUtils.class);
            MockedStatic<Reflections> reflections = Mockito.mockStatic(Reflections.class)) {
            reflections.when(() -> Reflections.getCraftBukkitClass(Mockito.eq("inventory.CraftItemStack")))
                    .thenAnswer((invocationOnMock) -> CraftItemStack.class);

            methodUtils.when(() -> MethodUtils.invokeStaticMethod(Mockito.any(Class.class), Mockito.eq("asNMSCopy"), Mockito.eq(itemStack)))
                    .thenThrow(NoSuchMethodException.class);

            net.minecraft.world.item.ItemStack copy = ItemStacks.asNMSCopy(itemStack);
            assertNull(copy);
        }
    }
}
