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
package eu.europa.esig.dss.ws.signature.soap;

import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.dto.ToBeSignedDTO;
import eu.europa.esig.dss.ws.signature.common.RemoteMultipleDocumentsSignatureService;
import eu.europa.esig.dss.ws.signature.dto.DataToSignMultipleDocumentsDTO;
import eu.europa.esig.dss.ws.signature.dto.ExtendDocumentDTO;
import eu.europa.esig.dss.ws.signature.dto.SignMultipleDocumentDTO;
import eu.europa.esig.dss.ws.signature.soap.client.SoapMultipleDocumentsSignatureService;

@SuppressWarnings("serial")
public class SoapMultipleDocumentsSignatureServiceImpl implements SoapMultipleDocumentsSignatureService {

	private RemoteMultipleDocumentsSignatureService service;

	public void setService(RemoteMultipleDocumentsSignatureService service) {
		this.service = service;
	}

	@Override
	public ToBeSignedDTO getDataToSign(DataToSignMultipleDocumentsDTO dataToSignDto) {
		return service.getDataToSign(dataToSignDto.getToSignDocuments(), dataToSignDto.getParameters());
	}

	@Override
	public RemoteDocument signDocument(SignMultipleDocumentDTO signDocumentDto) {
		return service.signDocument(signDocumentDto.getToSignDocuments(), signDocumentDto.getParameters(), signDocumentDto.getSignatureValue());
	}

	@Override
	public RemoteDocument extendDocument(ExtendDocumentDTO extendDocumentDto) {
		return service.extendDocument(extendDocumentDto.getToExtendDocument(), extendDocumentDto.getParameters());
	}

}
