package com.rodolfo.ulcer.segmentation.services.impl;

import java.io.FileNotFoundException;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.repositories.ImageRepository;
import com.rodolfo.ulcer.segmentation.services.ImageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageServiceImpl implements ImageService {

	private static final ImageRepository imageRepository = new ImageRepository();
	private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

	@Override
	public void openWithLabeled(Image image) {

		this.verifyImage(image);
		this.verifyLabeledImage(image);

		imageRepository.openWithLabeled(image);
	}

	@Override
	public void open(Image image) {
		
		this.verifyImage(image);
		imageRepository.open(image);
	}

	@Override
	public void save(Image image) {
		// TODO Auto-generated method stub

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
	}

	private void verifyLabeledImage(Image image) {

		if (image.getDirectory().getLabeledImagePath() == null || !image.getDirectory().getLabeledImagePath().exists()) {

			log.error("Erro inesperado ", new FileNotFoundException("Imagem rotulada não encontrada no caminho especificado"));
			System.exit(1);
		}
	}


}