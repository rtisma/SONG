package org.icgc.dcc.song.importer.config;

import lombok.Getter;
import lombok.val;
import org.icgc.dcc.song.importer.download.DownloadIterator;
import org.icgc.dcc.song.importer.model.DccMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import static org.icgc.dcc.song.importer.convert.DccMetadataUrlConverter.createDccMetadataUrlConverter;
import static org.icgc.dcc.song.importer.download.DownloadIterator.createDownloadIterator;
import static org.icgc.dcc.song.importer.download.urlgenerator.impl.DccMetadataUrlGenerator.createDccMetadataUrlGenerator;

@Configuration
@Lazy
@Getter
public class DccMetadataConfig {

  private static final int DCC_METADATA_MAX_FETCH_SIZE = 2000;
  private static final int DCC_METADATA_INITIAL_FROM = 0;

  @Value("${dcc-metadata.url}")
  private String url;

  @Value("${dcc-metadata.fetchSize}")
  private int fetchSize;

  @Bean
  public DownloadIterator<DccMetadata> dccMetadataDownloadIterator(){
    val urlGenerator = createDccMetadataUrlGenerator(url);
    val urlConverter = createDccMetadataUrlConverter();
    return createDownloadIterator(urlConverter,urlGenerator, fetchSize,
        DCC_METADATA_MAX_FETCH_SIZE, DCC_METADATA_INITIAL_FROM);
  }

}
