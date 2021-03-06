package com.rodolfo.ulcer.segmentation.services.impl;

import java.io.FileNotFoundException;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.repositories.ImageRepository;
import com.rodolfo.ulcer.segmentation.services.ImageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageServiceImpl implements ImageService {

	private static final ImageRepository IMAGE_REPOSITORY = new ImageRepository();

	@Override
	public void open(Image image) {

		this.verifyImage(image);

		IMAGE_REPOSITORY.open(image);
	}

	@Override
	public void save(Image image) {

		log.info("Salvando as imagens no caminho : {}", image.getDirectory().getDirPath().getAbsolutePath());

		if(image.getGrabCutHumanMask() != null) {

			IMAGE_REPOSITORY.save(image.getGrabCutHumanMask(), image.getDirectory().getGrabCutMaskPath());
		}

		if(image.getFinalUlcerSegmentation() != null) {

			IMAGE_REPOSITORY.save(image.getFinalUlcerSegmentation(), image.getDirectory().getGrabCutSegmentationPath());
		}

		if(image.getFinalBinarySegmentation() != null) {

			IMAGE_REPOSITORY.save(image.getFinalBinarySegmentation(), image.getDirectory().getGrabCutSegmentationBinaryPath());
		}

		if(image.getMlCLassifiedImage() != null) {

			IMAGE_REPOSITORY.save(image.getMlCLassifiedImage(), image.getDirectory().getSvmClassificatioPath());
		}

		if(image.getSkeletonWithBranchs() != null) {

			IMAGE_REPOSITORY.save(image.getSkeletonWithBranchs(), image.getDirectory().getSkeletonWithBranchsPath());
		}

		if(image.getSkeletonWithoutBranchs() != null) {

			IMAGE_REPOSITORY.save(image.getSkeletonWithoutBranchs(), image.getDirectory().getSkeletonWithoutBranchsPath());
		}

		if(image.getImageWithoutReflection() != null) {

			IMAGE_REPOSITORY.save(image.getImageWithoutReflection(), image.getDirectory().getImageWithoutReflectionsPath());
		}
	
		if(image.getLabeledImage() != null) {

			IMAGE_REPOSITORY.save(image.getLabeledImage(), image.getDirectory().getLabeledResampleImagePath());
		}

		if(image.getSuperpixelsContourImage() != null) {

			IMAGE_REPOSITORY.save(image.getSuperpixelsContourImage(), image.getDirectory().getSuperpixelsLabelsPath());
		}

		if(image.getSuperpixelsColorInformativeImage() != null) {

			IMAGE_REPOSITORY.save(image.getSuperpixelsColorInformativeImage(), image.getDirectory().getSuperpixelsInformationalPath());
		}

		if(image.getMlOverlappingImage() != null) {

			IMAGE_REPOSITORY.save(image.getMlOverlappingImage(), image.getDirectory().getSvmOverlappingPath());
		}

		if(image.getGrabCutOverlappingImage() != null) {

			IMAGE_REPOSITORY.save(image.getGrabCutOverlappingImage(), image.getDirectory().getGrabCutOverlappingPath());
		}
	}

	private void verifyImage(Image image) {

		if (image.getDirectory() == null) {

			log.error("Erro inesperado ", new IllegalArgumentException("Imagem sem caminho especificado"));
			System.exit(1);
		}

		if (image.getDirectory().getImagePath() == null || !image.getDirectory().getImagePath().exists()) {

			log.error("Erro inesperado ", new FileNotFoundException("Imagem não encontrada no caminho especificado"));
			System.exit(1);
		}

		if (image.getDirectory().getLabeledImagePath() == null || !image.getDirectory().getLabeledImagePath().exists()) {

			log.warn("Caminho informado não possui a imagem rotulada, '{}'", 
				image.getDirectory().getDirPath() != null 
				? image.getDirectory().getDirPath().getAbsolutePath()
				: "Caminho absoluto não encontrado"
			);

			image.getDirectory().setLabeledImagePath(image.getDirectory().getImagePath());
		}
	}
}