package in.bg.ByeByeBG.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClerkJwksProvider {

    @Value("${clerk.jwks-url}")
    private String jwksUrl;

    private final Map<String, PublicKey> keyCache = new HashMap<>();
    private long lastFetchTime = 0;
    private static final long CACHE_TTL = 3600000; //1 hour

    public PublicKey getPublicKey(String kid) throws Exception{
        if(keyCache.containsKey(kid) && System.currentTimeMillis() - lastFetchTime < CACHE_TTL){
            return keyCache.get(kid);
        }
        refreshKeys();
        return keyCache.get(kid);
    }

    private void refreshKeys() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jwks = mapper.readTree(new URL(jwksUrl));
        JsonNode keys = jwks.get("keys");
        for(JsonNode keyNode : keys){
            String kid=keyNode.get("kid").asText();
            String kty=keyNode.get("kty").asText();
            String alg=keyNode.get("alg").asText();

            if("RSA".equals(kty) && "RS256".equals(alg)){
                String n = keyNode.get("n").asText();
                String e = keyNode.get("e").asText();

                PublicKey publicKey = createPublicKey(n,e);
                keyCache.put(kid,publicKey);
            }
        }
        lastFetchTime = System.currentTimeMillis();
    }

    private PublicKey createPublicKey(String modulus, String exponent) throws Exception{
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);
        BigInteger modulusBigInt = new BigInteger(1,modulusBytes);
        BigInteger exponentBigInt = new BigInteger(1,exponentBytes);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusBigInt,exponentBigInt);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

}
