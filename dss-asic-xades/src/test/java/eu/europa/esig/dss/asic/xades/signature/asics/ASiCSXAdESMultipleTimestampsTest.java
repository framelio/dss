/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.asic.xades.signature.asics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import eu.europa.esig.dss.asic.common.ASiCExtractResult;
import eu.europa.esig.dss.asic.common.AbstractASiCContainerExtractor;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESContainerExtractor;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.test.signature.PKIFactoryAccess;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;

public class ASiCSXAdESMultipleTimestampsTest extends PKIFactoryAccess {

	@Test
	public void test() throws Exception {
		List<DSSDocument> documentToSigns = new ArrayList<DSSDocument>();
		documentToSigns.add(new InMemoryDocument("Hello World !".getBytes(), "test.text", MimeType.TEXT));
		documentToSigns.add(new InMemoryDocument("Bye World !".getBytes(), "test2.text", MimeType.TEXT));

		ASiCWithXAdESSignatureParameters signatureParameters = new ASiCWithXAdESSignatureParameters();
		signatureParameters.bLevel().setSigningDate(new Date());
		signatureParameters.setSigningCertificate(getSigningCert());
		signatureParameters.setCertificateChain(getCertificateChain());
		signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_T);
		signatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_S);

		ASiCWithXAdESService service = new ASiCWithXAdESService(getCompleteCertificateVerifier());
		service.setTspSource(getGoodTsa());

		ToBeSigned dataToSign = service.getDataToSign(documentToSigns, signatureParameters);
		SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());
		DSSDocument signedDocument = service.signDocument(documentToSigns, signatureParameters, signatureValue);

		ASiCWithXAdESSignatureParameters extendParameters = new ASiCWithXAdESSignatureParameters();
		extendParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_T);
		extendParameters.aSiC().setContainerType(ASiCContainerType.ASiC_S);
		DSSDocument extendDocument = service.extendDocument(signedDocument, extendParameters);

		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(extendDocument);
		validator.setCertificateVerifier(getCompleteCertificateVerifier());

		Reports reports = validator.validateDocument();

		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<String> signatureIdList = diagnosticData.getSignatureIdList();
		assertEquals(1, signatureIdList.size());
		assertTrue(diagnosticData.isBLevelTechnicallyValid(diagnosticData.getFirstSignatureId()));

		SignatureWrapper signatureWrapper = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		List<TimestampWrapper> timestampList = signatureWrapper.getTimestampList();
		assertEquals(2, timestampList.size());

		for (TimestampWrapper timestampWrapper : timestampList) {
			assertTrue(timestampWrapper.isSignatureValid());
		}

		AbstractASiCContainerExtractor extractor = new ASiCWithXAdESContainerExtractor(extendDocument);
		ASiCExtractResult result = extractor.extract();

		assertEquals(0, result.getUnsupportedDocuments().size());

		List<DSSDocument> signatureDocuments = result.getSignatureDocuments();
		assertEquals(1, signatureDocuments.size());
		String signatureFilename = signatureDocuments.get(0).getName();
		assertTrue(signatureFilename.startsWith("META-INF/signature"));
		assertTrue(signatureFilename.endsWith(".xml"));

		List<DSSDocument> manifestDocuments = result.getManifestDocuments();
		assertEquals(0, manifestDocuments.size());

		List<DSSDocument> signedDocuments = result.getSignedDocuments();
		assertEquals(1, signedDocuments.size());

		DSSDocument mimeTypeDocument = result.getMimeTypeDocument();

		byte[] mimeTypeContent = DSSUtils.toByteArray(mimeTypeDocument);
		try {
			assertEquals(MimeType.ASICS.getMimeTypeString(), new String(mimeTypeContent, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}

	}

	@Override
	protected String getSigningAlias() {
		return GOOD_USER;
	}

}
