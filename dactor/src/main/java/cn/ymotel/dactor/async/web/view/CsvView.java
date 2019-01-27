package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.LocalServletMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * 参数  _headers_key 定义输出顺序，为数组
 * _FileName 为下载的文件名称
 * _Content 为下载内容
 * @author wsl
 *
 */
public class CsvView extends DownloadView {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(CsvView.class);

	private static final String NEW_LINE_SEPARATOR = "\n";

	@Override
	public void renderInner(LocalServletMessage message, String defaultMessage) {
		List list = (List) message.getContext().get(this.getContent());

		CSVFormat csvformat=CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		{
		String[] headers1=(String[])message.getContext().get("_headers");
		
			if(headers1!=null){
				csvformat=csvformat.withHeader(headers1);
			}
		
		}
		try {
			StringBuilder sbuilder=new StringBuilder();
			CSVPrinter csvFilePrinter = new CSVPrinter(sbuilder, csvformat);
			String[] headers=(String[])message.getContext().get("_headers_key");

			for(int i=0;i<list.size();i++){
				Map tmpMap=(Map)list.get(i);
				List tmpList=new ArrayList();
					for(int j=0;j<headers.length;j++){
						tmpList.add(tmpMap.get(headers[j]));
					}
				
					csvFilePrinter.printRecord(tmpList);	
			}
			
			csvFilePrinter.flush();
			csvFilePrinter.close();
			
			message.getContext().put(this.getContent(), sbuilder.toString().getBytes("GBK"));
			
			if(message.getContext().containsKey(this.getFILE_NAME())){
				
			}else{
				java.text.SimpleDateFormat sdf =new java.text.SimpleDateFormat("yyyyMMddhhmmss");
				message.getContext().put(this.getFILE_NAME(), sdf.format(new java.util.Date())+".csv");
			}
			
			
//			message.getAsyncContext().getResponse().getWriter().flush();
//			message.getAsyncContext().getResponse().getWriter().flush();
//			message.getAsyncContext().complete();			
		} catch (IOException e) {
			if (logger.isTraceEnabled()) {
				logger.trace("renderInner(LocalServletMessage, String)"); //$NON-NLS-1$
			}
		}

		super.renderInner(message, defaultMessage);
	}

}
