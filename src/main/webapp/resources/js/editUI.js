                function decToHex(dec) {
                // converts deciman to hexadecimal number
                var hexStr = "0123456789ABCDEF";
                var low = dec % 16;
                var high = (dec - low)/16;
                hex = "" + hexStr.charAt(high) + hexStr.charAt(low);
                return hex;
            }

            function trim(s) {
                // clears leading and trailing  white spaces
                return s.replace( /^\s+/g, "" ).replace( /\s+$/g, "" );
            }

            function parseColor(cl) {
                // converts rgb(d, d, d) colorf format into #xxx coloro format
                if(cl.length==0) return '';
                if(cl.substring(0,1)=='#') return cl;
                var color_parts = cl.substring(4,cl.length-1).split(', ');
                return ('#' + decToHex(color_parts[0]) + decToHex(color_parts[1]) + decToHex(color_parts[2]));
            }

            function cellClick(event) {

                // get all cells from template grid
                celije = document.getElementById('temp').getElementsByTagName("td");

                // remove mark from previus selected cell
                for(i=0; i<celije.length; i++)
                    celije.item(i).style.border='';

                // little trick to catch event object for IE and Mozilla browsers
                e = event.srcElement || event.target;

                if(e.tagName == "IMG" || e.getElementsByTagName('IMG').length>0)
                    document.getElementsByName('vrsta')[1].checked=true;
                else
                    document.getElementsByName('vrsta')[0].checked=true;
                while(e.tagName!='TD') e=e.parentNode;
                e.style.border='medium solid rgb(255,0,0)';
                switchType();

            }

            function clearCell(node) {
                if(node.hasChildNodes()) node.firstChild.data='EMPTY CELL!!!';
                else node.appendChild(document.createTextNode('EMPTY CELL!!!'));
                node.style.fontSize='';
                node.style.fontFamily='';
                node.style.color='';
                node.style.fontStyle='';
                node.style.textDecoration='';
                node.style.fontWeight='';
                node.style.textAlign='';
                node.style.verticalAlign='';
                document.getElementsByName('vrsta')[0].checked=true;
                switchType();
            }

            function updateCell() {

                // get all cells from template grid
                celije = document.getElementById('temp').getElementsByTagName("td");
                currentCell=-1;

                // fing which cell is marked for update
                for(i=0; i<celije.length; i++) {
                    if(celije.item(i).style.border.length>0) {
                        currentCell=i;
                        break;
                    }
                }
                if(currentCell>-1) {
                    while (celije.item(currentCell).childNodes[0]) {
                        celije.item(currentCell).removeChild(celije.item(currentCell).childNodes[0]);
                    }
                    if(!document.getElementById('vrednost').value && !document.getElementById('slika').value) {
                        clearCell(celije.item(currentCell));
                        return;
                    }
                    if(document.getElementsByName('vrsta')[1].checked) {
                        if(document.getElementById('linkable').checked) {
                            var novilink = document.createElement('a');
                            novilink.href = document.getElementById('link').value;
                            var novaslika = document.createElement('IMG');
                            // nove vrednosti
                            novaslika.src=applicationRoot+"/images/gallery/"+document.getElementById('slika').value;
                            novaslika.alt=document.getElementById('slika').value;
                            novilink.appendChild(novaslika);
                            celije.item(currentCell).appendChild(novilink);
                        }
                        else {
                            var novaslika = document.createElement('IMG');
                            //novaslika.src=document.getElementById('slika').value;
                            //novaslika.alt='';
                            //Nova verzija sa altom
                            novaslika.src=applicationRoot+"/images/gallery/"+document.getElementById('slika').value;
                            novaslika.alt=document.getElementById('slika').value;
                            celije.item(currentCell).appendChild(novaslika);
                        }
                    }

                    else {
                        if(document.getElementById('linkable').checked) {
                            novilink = document.createElement('a');
                            novilink.href = document.getElementById('link').value;
                            novilink.style.color = document.getElementById('boja').value;
                            novilink.appendChild(document.createTextNode((document.getElementById('vrednost').value.length)?document.getElementById('vrednost').value:'EMPTY CELL!!!'));
                            celije.item(currentCell).appendChild(novilink);
                        }
                        else {
                            celije.item(currentCell).appendChild(document.createTextNode((document.getElementById('vrednost').value.length)?document.getElementById('vrednost').value:'EMPTY CELL!!!'));
                            document.getElementById('link').value = 'http:\/\/';
                            document.getElementById('link').disabled = true;
                        }

                        celije.item(currentCell).style.fontSize = document.getElementById('size').value;
                        celije.item(currentCell).style.fontFamily = document.getElementById('tip').value;
                        celije.item(currentCell).style.color = document.getElementById('boja').value;

                        if(document.getElementById('bold').checked) celije.item(currentCell).style.fontWeight='bold';
                        else celije.item(currentCell).style.fontWeight='';
                        if(document.getElementById('italic').checked) celije.item(currentCell).style.fontStyle='italic';
                        else celije.item(currentCell).style.fontStyle='';
                        if(document.getElementById('underline').checked) celije.item(currentCell).style.textDecoration='underline';
                        else celije.item(currentCell).style.textDecoration='';
                    }

                    for(i=0;i<3;i++) {
                        if(document.getElementsByName('pozicija')[i].checked) {
                            celije.item(currentCell).style.textAlign = document.getElementsByName('pozicija')[i].value;
                            break;
                        }
                    }
                    for(i=0;i<3;i++) {
                        if(document.getElementsByName('verticalAlign')[i].checked) {
                            celije.item(currentCell).style.verticalAlign = document.getElementsByName('verticalAlign')[i].value;
                            break;
                        }
                    }
                  }
            }

            function saveForm(){
                // get all cells from template grid
                celije = document.getElementById('temp').getElementsByTagName("td");
                 for(i=0; i<celije.length; i++) {
                         if(celije.item(i).getElementsByTagName('A').length>0){
                             if(celije.item(i).getElementsByTagName('IMG').length>0){
                                 // Proba sa altom
                                 // document.getElementById('cell'+i+'content').value=celije.item(i).getElementsByTagName('IMG').item(0).src;
                                 document.getElementById('cell'+i+'content').value=celije.item(i).getElementsByTagName('IMG').item(0).alt;
                                 document.getElementById('cell'+i+'type').value="LinkWithPic";
                             }
                             else{
                                 document.getElementById('cell'+i+'type').value="Link";
                                 if(celije.item(i).getElementsByTagName('A').item(0).childNodes.item(0).data=='EMPTY CELL!!!'){
                                     document.getElementById('cell'+i+'content').value=celije.item(i).getElementsByTagName('A').item(0).href;

                                 }else{

                                     document.getElementById('cell'+i+'content').value=trim(celije.item(i).getElementsByTagName('A').item(0).childNodes.item(0).data)

                                 }
                             }
                                 document.getElementById('cell'+i+'link').value=celije.item(i).getElementsByTagName('A').item(0).href;

                         }else{
                             if(celije.item(i).getElementsByTagName('IMG').length>0){
                                 // Proba sa altom
                                 //document.getElementById('cell'+i+'content').value=celije.item(i).getElementsByTagName('IMG').item(0).src;
                                 document.getElementById('cell'+i+'content').value=celije.item(i).getElementsByTagName('IMG').item(0).alt;
                                 document.getElementById('cell'+i+'type').value="Picture";
                             }else{

                                 if(trim(celije.item(i).childNodes.item(0).data)=='EMPTY CELL!!!'){
                                     document.getElementById('cell'+i+'content').value='';
                                     document.getElementById('cell'+i+'type').value="blank";
                                 }else{
                                     document.getElementById('cell'+i+'content').value=trim(celije.item(i).childNodes.item(0).data);
                                     document.getElementById('cell'+i+'type').value="Text";
                                 }
                             }
                         }
                     document.getElementById('cell'+i+'fontsize').value = celije.item(i).style.fontSize;
                    document.getElementById('cell'+i+'font').value = celije.item(i).style.fontFamily;
                    document.getElementById('cell'+i+'color').value = parseColor(celije.item(i).style.color);
                    document.getElementById('cell'+i+'position').value = celije.item(i).style.textAlign;
                    document.getElementById('cell'+i+'vertical').value= celije.item(i).style.verticalAlign;
                    if(celije.item(i).style.textDecoration) document.getElementById('cell'+i+'fontstyle').value=document.getElementById('cell'+i+'fontstyle').value+'u';
                    if(celije.item(i).style.fontStyle) document.getElementById('cell'+i+'fontstyle').value=document.getElementById('cell'+i+'fontstyle').value+'i';
                    if(celije.item(i).style.fontWeight) document.getElementById('cell'+i+'fontstyle').value=document.getElementById('cell'+i+'fontstyle').value+'b';
                 }


            }
            function switchType() {
                celije = document.getElementById('temp').getElementsByTagName("td");
                currentCell=-1;

                // fing which cell is marked for update
                for(i=0; i<celije.length; i++) {
                    if(celije.item(i).style.border.length>0) {
                        currentCell=i;
                        break;
                    }
                }
                var empty=true;
                var hasLink=false;
                if(document.getElementsByName('vrsta')[0].checked) {
                    document.getElementById('vrednost').disabled=false;
                    document.getElementById('slika').disabled=true;
                    document.getElementById('slika').value='';
                    document.getElementById('imgManager').style.visibility='hidden';
                    document.getElementById('colpik').style.visibility='visible';
                    if(currentCell>-1) {
                        if(celije.item(currentCell).getElementsByTagName('A').length>0)
                            hasLink=true;
                        else
                            hasLink=false;
                        var labelText;
                        if(hasLink)
                            labelText=trim(celije.item(currentCell).getElementsByTagName('A')[0].firstChild.data);
                        else
                            labelText=trim(celije.item(currentCell).firstChild.data);
                        if(labelText=="EMPTY CELL!!!")
                            empty=true;
                        else {
                            empty=false;
                            document.getElementById('vrednost').value=labelText;
                            if(hasLink) {
                                document.getElementById('link').value=celije.item(currentCell).getElementsByTagName('A')[0].href;
                                document.getElementById('link').disabled=false;
                                document.getElementById('linkable').checked=true;
                            }
                            else {
                                document.getElementById('link').value="http:\/\/";
                                document.getElementById('link').disabled=true;
                                document.getElementById('linkable').checked=false;
                            }
                            document.getElementById('tip').disabled=false;
                            document.getElementById('tip').value = celije.item(currentCell).style.fontFamily;
                            document.getElementById('size').disabled=false;
                            document.getElementById('size').value = celije.item(currentCell).style.fontSize;
                            document.getElementById('boja').disabled=false;
                            document.getElementById('boja').value = parseColor(celije.item(currentCell).style.color);
                            switch(celije.item(currentCell).style.textAlign) {
                                case 'center':
                                    document.getElementsByName('pozicija')[1].checked = true;
                                    break;
                                case 'right':
                                    document.getElementsByName('pozicija')[2].checked = true;
                                    break;
                                case 'left':
                                    document.getElementsByName('pozicija')[0].checked = true;
                                    break;
                                default:
                                    document.getElementsByName('pozicija')[0].checked = false;
                                    document.getElementsByName('pozicija')[1].checked = false;
                                    document.getElementsByName('pozicija')[2].checked = false;
                            }
                            switch(celije.item(currentCell).style.verticalAlign) {
                                case 'middle':
                                    document.getElementsByName('verticalAlign')[1].checked = true;
                                    break;
                                case 'bottom':
                                    document.getElementsByName('verticalAlign')[2].checked = true;
                                    break;
                                case 'top':
                                    document.getElementsByName('verticalAlign')[0].checked = true;
                                    break;
                                default:
                                    document.getElementsByName('verticalAlign')[0].checked = false;
                                    document.getElementsByName('verticalAlign')[1].checked = false;
                                    document.getElementsByName('verticalAlign')[2].checked = false;
                            }
                            document.getElementById('italic').disabled=false;
                            if(celije.item(currentCell).style.fontStyle) document.getElementById('italic').checked=true;
                            else document.getElementById('italic').checked=false;
                            document.getElementById('bold').disabled=false;
                            if(celije.item(currentCell).style.fontWeight) document.getElementById('bold').checked=true;
                            else document.getElementById('bold').checked=false;
                            document.getElementById('underline').disabled=false;
                            if(celije.item(currentCell).style.textDecoration) document.getElementById('underline').checked=true;
                            else document.getElementById('underline').checked=false;
                        }
                    }
                    if(currentCell==-1 || empty) {
                        document.getElementById('vrednost').value='';
                        document.getElementById('link').value="http:\/\/";
                        document.getElementById('link').disabled=true;
                        document.getElementById('linkable').checked=false;
                        document.getElementById('size').disabled=false;
                        document.getElementById('size').value='';
                        document.getElementById('tip').disabled=false;
                        document.getElementById('tip').value='';
                        document.getElementById('boja').disabled=false;
                        document.getElementById('boja').value='';
                        document.getElementById('colpik').style.visibility='visible';
                        document.getElementById('bold').disabled=false;
                        document.getElementById('bold').checked=false;
                        document.getElementById('italic').disabled=false;
                        document.getElementById('italic').checked=false;
                        document.getElementById('underline').disabled=false;
                        document.getElementById('underline').checked=false;
                        document.getElementsByName('pozicija')[0].checked = false;
                        document.getElementsByName('pozicija')[1].checked = false;
                        document.getElementsByName('pozicija')[2].checked = false;
                        document.getElementsByName('verticalAlign')[0].checked = false;
                        document.getElementsByName('verticalAlign')[1].checked = false;
                        document.getElementsByName('verticalAlign')[2].checked = false;
                    }
                }
                else {
                    document.getElementById('slika').disabled=false;
                    document.getElementById('imgManager').style.visibility='visible';
                    document.getElementById('vrednost').disabled=true;
                    document.getElementById('vrednost').value='';
                    document.getElementById('size').disabled=true;
                    document.getElementById('size').value='';
                    document.getElementById('tip').disabled=true;
                    document.getElementById('tip').value='';
                    document.getElementById('boja').disabled=true;
                    document.getElementById('boja').value='';
                    document.getElementById('colpik').style.visibility='hidden';
                    document.getElementById('bold').disabled=true;
                    document.getElementById('bold').checked=false;
                    document.getElementById('italic').disabled=true;
                    document.getElementById('italic').checked=false;
                    document.getElementById('underline').disabled=true;
                    document.getElementById('underline').checked=false;

                    if(currentCell>-1) {
                        if(celije.item(currentCell).getElementsByTagName('A').length>0) {
                            document.getElementById('link').disabled=false;
                            document.getElementById('linkable').checked=true;
                            document.getElementById('link').value=celije.item(currentCell).getElementsByTagName('A')[0].href;
                            // proba sa altom
                            // document.getElementById('slika').value=celije.item(currentCell).getElementsByTagName("IMG")[0].src;
                            document.getElementById('slika').value=celije.item(currentCell).getElementsByTagName("IMG")[0].alt;
                        }
                        else {
                            if(celije.item(currentCell).getElementsByTagName('IMG').length>0){
                            document.getElementById('link').disabled=true;
                            document.getElementById('linkable').checked=false;
                            document.getElementById('link').value='http:\/\/';
                            // proba sa altom
                            // document.getElementById('slika').value=celije.item(currentCell).getElementsByTagName("IMG")[0].src;
                            document.getElementById('slika').value=celije.item(currentCell).getElementsByTagName("IMG")[0].alt;
                            }
                        }
                        switch(celije.item(currentCell).style.textAlign) {
                            case 'center':
                                document.getElementsByName('pozicija')[1].checked = true;
                                break;
                            case 'right':
                                document.getElementsByName('pozicija')[2].checked = true;
                                break;
                            case 'left':
                                document.getElementsByName('pozicija')[0].checked = true;
                                break;
                            default:
                                document.getElementsByName('pozicija')[0].checked = false;
                                document.getElementsByName('pozicija')[1].checked = false;
                                document.getElementsByName('pozicija')[2].checked = false;
                        }
                        switch(celije.item(currentCell).style.verticalAlign) {
                            case 'middle':
                                document.getElementsByName('verticalAlign')[1].checked = true;
                                break;
                            case 'bottom':
                                document.getElementsByName('verticalAlign')[2].checked = true;
                                break;
                            case 'top':
                                document.getElementsByName('verticalAlign')[0].checked = true;
                                break;
                            default:
                                document.getElementsByName('verticalAlign')[0].checked = false;
                                document.getElementsByName('verticalAlign')[1].checked = false;
                                document.getElementsByName('verticalAlign')[2].checked = false;
                        }
                    }
                    else {
                        document.getElementById('slika').value='';
                        document.getElementById('link').disabled=true;
                        document.getElementById('linkable').checked=false;
                        document.getElementById('link').value='http:\/\/';
                        document.getElementsByName('pozicija')[0].checked = false;
                        document.getElementsByName('pozicija')[1].checked = false;
                        document.getElementsByName('pozicija')[2].checked = false;
                        document.getElementsByName('verticalAlign')[0].checked = false;
                        document.getElementsByName('verticalAlign')[1].checked = false;
                        document.getElementsByName('verticalAlign')[2].checked = false;

                    }
                }
            }

            function imageManage() {
                var w = 550 , h = 450;
                move = screen ? ',left=' + ((screen.width - w) >> 1) + ',top=' + ((screen.height - h) >> 1) : '',
                childWin = window.open(applicationRoot+'/do/editUI/imageManagerSetup', null, "help=no,status=no,scrollbars=yes,resizable=yes" + move + ",width=" + w + ",height=" + h + ",dependent=yes", true);
                childWin.opener = window;
                childWin.focus();
            }
