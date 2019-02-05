{\rtf1\ansi\ansicpg936\cocoartf1671\cocoasubrtf100
{\fonttbl\f0\fswiss\fcharset0 Helvetica-Bold;\f1\fswiss\fcharset0 Helvetica;\f2\fnil\fcharset134 PingFangSC-Regular;
}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
{\*\listtable{\list\listtemplateid1\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{decimal\}.}{\leveltext\leveltemplateid1\'02\'00.;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid1}
{\list\listtemplateid2\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{decimal\}.}{\leveltext\leveltemplateid101\'02\'00.;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid2}}
{\*\listoverridetable{\listoverride\listid1\listoverridecount0\ls1}{\listoverride\listid2\listoverridecount0\ls2}}
\paperw11900\paperh16840\margl1440\margr1440\vieww13160\viewh11620\viewkind0
\deftab420
\pard\pardeftab420\ri720\qj\partightenfactor0

\f0\b\fs28 \cf0 ReadMe \'96Lab3: Synchronization\
\
\pard\pardeftab420\ri-1133\qj\partightenfactor0

\fs24 \cf0 1. Write Peroxide.java
\fs28 \

\f1\b0\fs24 \expnd0\expndtw0\kerning0
In this lab, 
\fs22 \kerning1\expnd0\expndtw0 Create a new file called Peroxide.java. The atomic for-mula for Hydrogen Peroxide is H
\fs16 2
\fs22 O
\fs16 2
\fs22 . Write a synchronization program to generate Hydrogen Peroxide molecules
\f2 . Based on JNachosLab2Solution.\

\f1\fs24 \expnd0\expndtw0\kerning0
\
\
\pard\tx566\pardeftab420\ri-2318\partightenfactor0

\f0\b \cf0 2. Class \kerning1\expnd0\expndtw0 HAtom:\
\pard\pardeftab420\ri-1237\qj\partightenfactor0
\ls1\ilvl0
\f1\b0 \cf0 First, mutex semaphore calls P() to allocate one resource for mutex.\
\pard\pardeftab420\ri-1202\qj\partightenfactor0
\ls1\ilvl0\cf0 Then for first hAtom, the count for the first H has initiate value as 0. So count % 2 = 0. The count will change from 0 to 1. Then mutex semaphore calls V() to release one resource. This is used for protecting only add one to count in one hAtom. The first H will wait for the seconde hAtom. Wait calls P().\
\pard\pardeftab420\ri-1179\qj\partightenfactor0
\ls1\ilvl0\cf0 Then for the second hAtom, the count is 1, so count % 2 != 0. Then count add one. Then mutex semaphore calls V() to release one resource. Then the first hAtom and two OAtom which has already waited will be waked up. Wait calls P(). However, there are still two wait resource, so the three hAtoms will wait for the wait.V() releasing resource in OAtom.\
\pard\tx566\pardeftab420\ri720\qj\partightenfactor0
\cf0 \
\
\pard\pardeftab420\ri720\qj\partightenfactor0
\ls2\ilvl0
\f0\b \cf0 3. Class OAtom:\
\pard\pardeftab420\ri-1085\qj\partightenfactor0
\ls2\ilvl0
\f1\b0 \cf0 \expnd0\expndtw0\kerning0
For OAtom, \kerning1\expnd0\expndtw0 the first H will wait for the seconde hAtom. mutex2 semaphore calls P() to allocate one resource for mutex2.\expnd0\expndtw0\kerning0
 \kerning1\expnd0\expndtw0 Then for first OAtom, the count for the first O has initiate value as 0. So count % 2 = 0. The count will change from 0 to 1. Then mutex2 semaphore calls V() to release one resource. Wait1 calls P().The first O will wait for the seconde OAtom. \
\pard\pardeftab420\ri-1179\qj\partightenfactor0
\ls2\ilvl0\cf0 Then for the second hAtom, the count is 1, so count % 2 != 0. Then count add one. Then mutex2 semaphore calls V() to release one resource. \
\pard\pardeftab420\ri-1085\qj\partightenfactor0
\ls2\ilvl0\cf0 \expnd0\expndtw0\kerning0
Then two wait.V() will be called to wake up OAtoms and one wait1.V() will be called to wake up hAtoms. Then Hcount minus two and Ocount minus two to generate one peroxide. The mutex1.P() and mute1.V() called here is used for protecting generating one peroxide one single process. Then it will continue to create another peroxide.\
\
\
\
\
}