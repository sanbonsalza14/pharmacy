package com.my.pharmacy.controller;

import com.my.pharmacy.dto.DocumentDto;
import com.my.pharmacy.dto.InputDto;
import com.my.pharmacy.dto.KakaoApiResponseDto;
import com.my.pharmacy.dto.OutputDto;
import com.my.pharmacy.entity.Pharmacy;
import com.my.pharmacy.service.KakaoAddressSearchService;
import com.my.pharmacy.service.KakaoCategorySearchService;
import com.my.pharmacy.service.PharmacyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FormController {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final PharmacyService pharmacyService;

    public FormController(
            KakaoAddressSearchService kakaoAddressSearchService,
            KakaoCategorySearchService kakaoCategorySearchService,
            PharmacyService pharmacyService
    ) {
        this.kakaoAddressSearchService = kakaoAddressSearchService;
        this.kakaoCategorySearchService = kakaoCategorySearchService;
        this.pharmacyService = pharmacyService;
    }

    /** 메인 입력 폼 */
    @GetMapping("/")
    public String mainForm() {
        return "main";
    }

    /** 결과 페이지 */
    @GetMapping("/output")
    public String outputForm() {
        return "output";
    }

    /**
     * 주소로 검색 → 좌표 획득 → 반경 내 약국 조회 → 화면용 DTO 생성 → DB 저장 → 결과 뷰 반환
     */
    @PostMapping("/search")
    public String searchAddress(InputDto dto, Model model) {
        // 1) 주소 검색
        KakaoApiResponseDto addr = kakaoAddressSearchService.requestAddressSearch(dto.getAddress());
        if (addr == null || addr.getDocumentList() == null || addr.getDocumentList().isEmpty()) {
            model.addAttribute("outputList", List.of());
            model.addAttribute("message", "해당 주소로 검색된 결과가 없습니다.");
            return "output";
        }

        // 2) 기준 좌표(첫 결과)
        DocumentDto base = addr.getDocumentList().get(0);

        // 3) 반경 내 카테고리(약국) 검색
        double radius = 1000; // m
        KakaoApiResponseDto rec = kakaoCategorySearchService.resultCategorySearch(
                base.getLatitude(), base.getLongitude(), radius
        );
        List<DocumentDto> docs = (rec == null) ? List.of() : rec.getDocumentList();

        // 4) 화면용 DTO 가공
        List<OutputDto> output = kakaoCategorySearchService.makeOutputDto(docs);

        // 5) DB 저장 (서비스에서 중복 필터링/유니크 제약 처리)
        if (docs != null && !docs.isEmpty()) {
            pharmacyService.saveFromDocuments(docs);
        }

        // 6) 모델 바인딩 후 뷰 반환
        model.addAttribute("outputList", output);
        return "output";
    }

    /** ✅ REST 확인용: 저장된 약국 전체 JSON으로 반환 (포스트맨/덤프 확인) */
    @GetMapping("/api/pharmacies")
    @ResponseBody
    public List<Pharmacy> listAll() {
        return pharmacyService.findAll();
    }
}
