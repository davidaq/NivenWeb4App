import config
import os
from parsews import parsews

primitivetypes = {
	'Date':'NSDate',
	'String':'NSString',
	'Integer':'NWAInteger',
	'Long':'NWALong',
	'Float':'NWAFloat',
	'Double':'NWADouble',
	'Byte':'NWAByte',
	'Character':'NWAChar',
	'Boolean':'NWABool',
	'void':'NWAVoid'
}

def typemap(t):
	isarray = False
	if t[-2:] == '[]':
		isarray = True
		t = t[:-2]
	try:
		ret = primitivetypes[t]
	except:
		ret = config.prefix + t
	if isarray:
		ret = 'ArrayOf' + ret
	return ret

class parseios(parsews):
	
	fphead = None
	fpbody = None
	
	def begin(self):
		try:
			os.makedirs(config.path)
		except:
			pass
		self.fphead = open(config.path + config.filename + '.h', 'w')
		self.fpbody = open(config.path + config.filename + '.m', 'w')
		
		self.fphead.write('#import "NWABegin.h"\n')
		self.fpbody.write('#import "' + config.filename + '.h"\n')
		
		self.fpbody.write('#import "NWABody.h"\n')
		
		for type in primitivetypes:
			self.fphead.write('NWA_BEAN_ARRAY(' + primitivetypes[type] + ')\n')
			self.fpbody.write('NWA_BEAN_ARRAY_BODY(' + primitivetypes[type] + ')\n')

	def makebean(self,dict):
		self.fphead.write('\nNWA_BEAN_BEGIN(' + config.prefix + dict['name'] + ')\n')
		for field in dict['field']:
			self.fphead.write('NWA_FIELD(' + typemap(field['type']) + ',' + field['name'] + ')\n')
		self.fphead.write('NWA_BEAN_END(' + config.prefix + dict['name'] + ')\n\n')
		
		self.fpbody.write('\n@implementation ' + config.prefix + dict['name'] + '\n');
		self.fpbody.write('NWA_BEAN_PACK_BEGIN\n')
		for field in dict['field']:
			self.fpbody.write('NWA_BEAN_PACK(' + field['name'] + ')\n')
		self.fpbody.write('NWA_BEAN_PACK_END\n\n')
		self.fpbody.write('NWA_BEAN_UNPACK_BEGIN\n')
		for field in dict['field']:
			self.fpbody.write('NWA_BEAN_UNPACK(' + typemap(field['type']) + ',' + field['name'] + ')\n')
		self.fpbody.write('NWA_BEAN_UNPACK_END\n')
		self.fpbody.write('@end\n');
		self.fpbody.write('NWA_BEAN_ARRAY_BODY(' + config.prefix + dict['name'] + ')\n\n')
	
	def actiondef(self,out,action):
		out.write('+(void)' + action['name'] + '_')
		for param in action['parameter']:
			out.write(param['name'] + ':(' + typemap(param['type']) + '*)' + param['name'] + '_\n')
		out.write('onResult:(void(^)(int errorCode, NSString *errorMsg, ' + typemap(action['return']) + '* result))onResult')
		
	def makegroup(self,dict):
		self.fphead.write('\n@interface ' + config.prefix + dict['name'] + ': NSObject\n');
		for action in dict['action']:
			self.actiondef(self.fphead,action)
			self.fphead.write(';\n')
		self.fphead.write('@end\n\n');
	
		self.fpbody.write('\n@implementation ' + config.prefix + dict['name'] + '\n');
		for action in dict['action']:
			self.actiondef(self.fpbody,action)
			self.fpbody.write(' { \n')
			self.fpbody.write('\tNWARequest* __request = [[NWARequest alloc]init];\n')
			self.fpbody.write('\t__request.url = @"' + action['url'] + '";\n')
			for param in action['parameter']:
				self.fpbody.write('\t[__request put:' + param['name'] + '_ forParam:@"' + param['name'] + '"];\n')
			self.fpbody.write('\t[__request onResult:^(int errorCode, NSString *errorMsg, id result) {\n')
			self.fpbody.write('\t\tif(errorCode) onResult(errorCode, errorMsg, nil);\n')
			if action['return'] == 'void':
				self.fpbody.write('\t\telse onResult(errorCode, errorMsg, nil);\n')
			else:
				self.fpbody.write('\t\telse onResult(errorCode, errorMsg, [NWAPack unpack:result to:' + typemap(action['return']) + '.class]);\n')
			self.fpbody.write('\t}];\n')
			self.fpbody.write('\t[__request fire];\n')
			self.fpbody.write('}\n')
		self.fpbody.write('@end\n\n');
		
	def end(self):
		self.fphead.write('#import "NWAEnd.h"\n')
		
		self.fphead.close()
		self.fpbody.close()