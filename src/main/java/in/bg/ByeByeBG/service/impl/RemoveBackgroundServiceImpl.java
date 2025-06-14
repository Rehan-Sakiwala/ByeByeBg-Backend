package in.bg.ByeByeBG.service.impl;

import in.bg.ByeByeBG.client.ClipdropClient;
import in.bg.ByeByeBG.service.RemoveBackgroundService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RemoveBackgroundServiceImpl implements RemoveBackgroundService {

    @Value("${clipdrop.key}")
    private String api_key;

    private final ClipdropClient clipdropClient;

    @Override
    public byte[] removeBackground(MultipartFile file) {
        return clipdropClient.removeBackground(file,api_key);
    }
}
