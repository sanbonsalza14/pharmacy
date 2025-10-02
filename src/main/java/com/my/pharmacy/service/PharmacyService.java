package com.my.pharmacy.service;


import com.my.pharmacy.dto.DocumentDto;
import com.my.pharmacy.entity.Pharmacy;
import com.my.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;


    @Transactional
    public void saveFromDocuments(List<DocumentDto> docs){
        List<Pharmacy> batch = docs.stream().map(d -> Pharmacy.builder()
                .name(d.getPlaceName())
                .address(d.getAddressName())
                .distance(d.getDistance())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .build()).toList();
        pharmacyRepository.saveAll(batch);
    }

    /** ✅ 컨트롤러에서 JSON 응답으로 쓰는 전체 조회 메서드 */
    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}