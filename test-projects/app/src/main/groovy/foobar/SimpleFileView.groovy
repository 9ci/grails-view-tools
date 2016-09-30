package foobar

import grails.plugin.viewtools.ViewResourceLocator
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.core.io.Resource
import org.springframework.util.StreamUtils
import org.springframework.web.servlet.view.AbstractUrlBasedView

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CompileStatic
class SimpleFileView extends AbstractUrlBasedView {

    ViewResourceLocator viewResourceLocator

    public SimpleFileView(){
        //setContentType("application/pdf");
    }


    @Override
    protected boolean generatesDownloadContent() {
        return false; //set to true to test files
    }

    @Override
    protected final void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Resource res = viewResourceLocator.getResource(url)

        StringBuffer contents = new StringBuffer()
        contents << res.inputStream.text << "<br/>"
        contents << "url: $url <br/>"
        contents << "view: $beanName <br/>"
        contents << "Resource: $res <br/>"

        response.with{
            contentType = getContentType()
            contentLength = contents.toString().bytes.size()//available()
            outputStream << contents
            outputStream.flush()
        }


    }

}





