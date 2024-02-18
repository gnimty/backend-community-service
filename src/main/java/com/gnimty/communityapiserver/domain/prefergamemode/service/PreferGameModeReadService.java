package com.gnimty.communityapiserver.domain.prefergamemode.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.domain.prefergamemode.repository.PreferGameModeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PreferGameModeReadService {

    private final PreferGameModeRepository preferGameModeRepository;

    public List<PreferGameMode> findByMember(Member member) {
        return preferGameModeRepository.findByMember(member);
    }
}
