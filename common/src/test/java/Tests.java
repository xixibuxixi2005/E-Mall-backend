import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.whut.emall.common.entitiy.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;

public class Tests {
    @Test
    public void testJwt() throws Exception{
        JwtUtils jwtUtils = new JwtUtils("sss");
        String token = jwtUtils.makeToken(new JwtPayload(114, "User1154"));
        JwtPayload payload = jwtUtils.verify(token);
        assertEquals("User1154", payload.getUsername());
    }
}
