# cms-core

### files
1. POST /cms/core/file  
@RequestParam MultipartFile file   
@RequestParam String folderName

2. PUT /cms/core/file  
{  
	"name": "file-name-to-be-changed",  
	"updateName": "new-file-name"  
}

3. DELETE /cms/core/file  
@RequestParam String folderName  
@RequestParam String fileName

4. GET /cms/core/file  
@RequestParam String folderName  
@RequestParam String fileName


### folders
1. POST /cms/core/folder  
{  
	"name": "new-folder-name",  
	"updateName": ""  
}

2. PUT /cms/core/folder  
{  
	"name": "old-name",  
	"updateName": "new-name"  
}

3. DELETE /cms/core/folder  
{  
	"name": "folder-name-to-be-deleted",  
	"updateName": ""  
}

4. GET /cms/core/folder  
{  
	"name": "folder-name-to-get",  
	"updateName": ""  
}