package de.timeout.libs.skin.mineskin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pgssoft.httpclient.HttpClientMock;
import de.timeout.libs.reflect.Reflections;
import org.apache.commons.lang.reflect.FieldUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class MineskinBuilderTest extends EasyMockSupport {

    HttpClientMock mock;

    JsonObject answer;

    @Before
    public void mock() throws ReflectiveOperationException, FileNotFoundException {
        this.mock = new HttpClientMock();

        setFinalStaticField(MineskinHandler.class, "client", mock);

        this.answer = new JsonParser()
                .parse(new FileReader(Paths.get("src", "test", "resources", "mineskin", "skin.json").toFile()))
                .getAsJsonObject();
    }

    @Test
    public void shouldGenerateMineskinSkin() throws ExecutionException, InterruptedException, TimeoutException {
        mock.onPost()
                .withHost("api.mineskin.org")
                .withPath("/generate/url")
                .doReturn(200, this.answer.toString());

        Mineskin skin = new MineskinBuilder()
                .name("test")
                .variant(Mineskin.Variant.CLASSIC)
                .visibility(Mineskin.Visibility.PRIVATE)
                .generate("http://textures.minecraft.net/texture/81478486b68fd0c9255bd4ac7ff28d1ab8db6d81eda01eecb319020985feca9f")
                .get(5, TimeUnit.SECONDS);

        assertNotNull(skin);
        assertEquals("test", skin.getName());
        assertTrue(skin.isPrivateSkin());
    }

    private static void setFinalStaticField(Class<?> clazz, String fieldName, Object value)
            throws ReflectiveOperationException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }
}
